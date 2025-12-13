import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { catchError, finalize, of } from 'rxjs';
import { ConfigService } from './config.service';
import { ConfigSummary, DiffResponse, ReportResponse, UploadResponse } from './types';

@Injectable({ providedIn: 'root' })
export class ConfigStore {
  private readonly api = inject(ConfigService);

  readonly listLoading = signal(false);
  readonly listError = signal<string | null>(null);
  readonly deletingId = signal<string | null>(null);

  readonly configs = signal<ConfigSummary[]>([]);

  readonly uploadJson = signal('');
  private uploadFile?: File;
  private selectedConfigId: string | null = null;
  readonly uploadResult = signal<UploadResponse | null>(null);
  readonly uploadLoading = signal(false);
  readonly uploadError = signal<string | null>(null);
  readonly uploadDisabled = computed(() => {
    return this.uploadLoading() || !!this.uploadError();
  });

  readonly compareResult = signal<DiffResponse | null>(null);
  readonly compareLoading = signal(false);

  readonly report = signal<ReportResponse | null>(null);
  readonly reportLoading = signal(false);

  readonly firstId = signal('');
  readonly secondId = signal('');
  readonly canCompare = computed(() => {
    const a = this.firstId();
    const b = this.secondId();
    return !!a && !!b && a !== b && !this.compareLoading();
  });

  constructor() {
    this.refresh();
    effect(() => {
      const ids = new Set(this.configs().map((cfg) => cfg.id));
      if (!ids.has(this.firstId())) this.firstId.set('');
      if (!ids.has(this.secondId())) this.secondId.set('');
    });
  }

  refresh(): void {
    this.listLoading.set(true);
    this.listError.set(null);
    this.api
      .listConfigs()
      .pipe(
        catchError((err) => {
          this.listError.set(err?.error?.message ?? 'Unable to load configs');
          return of<ConfigSummary[]>([]);
        }),
        finalize(() => this.listLoading.set(false))
      )
      .subscribe((configs) => this.configs.set(configs));
  }

  setUploadJson(value: string): void {
    this.uploadJson.set(value);
    this.uploadResult.set(null);
    this.uploadError.set(null);
    this.selectedConfigId = this.tryExtractConfigId(value);
    this.ensureNotDuplicate();
  }

  async setUploadFile(file?: File): Promise<void> {
    this.uploadFile = file;
    this.uploadResult.set(null);
    this.uploadError.set(null);
    this.selectedConfigId = null;
    if (file) {
      try {
        const text = await file.text();
        this.selectedConfigId = this.tryExtractConfigId(text);
      } catch {
        this.uploadError.set('Could not read file');
        return;
      }
    }
    this.ensureNotDuplicate();
  }

  upload(): void {
    if (this.ensureNotDuplicate()) return;
    if (this.uploadDisabled()) return;
    this.uploadLoading.set(true);
    this.uploadError.set(null);
    this.api.upload(this.uploadFile, this.uploadJson()).subscribe({
      next: (res) => {
        this.uploadResult.set(res);
        this.uploadLoading.set(false);
        this.refresh();
      },
      error: (err) => {
        const message = err?.error?.message || 'Upload failed';
        this.uploadError.set(message);
        this.uploadResult.set({
          id: '',
          message,
          validation: err?.error?.validation ?? { warnings: [], errors: [] },
          analyzer: err?.error?.analyzer ?? { warnings: [], errors: [] }
        });
        this.uploadLoading.set(false);
      }
    });
  }

  setFirstId(id: string): void {
    this.firstId.set(id);
    this.compareResult.set(null);
  }

  setSecondId(id: string): void {
    this.secondId.set(id);
    this.compareResult.set(null);
  }

  runCompare(): void {
    if (!this.canCompare()) return;
    this.compareLoading.set(true);
    this.api.compare(this.firstId(), this.secondId()).subscribe({
      next: (diff) => {
        this.compareResult.set(diff);
        this.compareLoading.set(false);
      },
      error: () => {
        this.compareResult.set(null);
        this.compareLoading.set(false);
      }
    });
  }

  loadReport(id: string): void {
    this.reportLoading.set(true);
    this.api.report(id).subscribe({
      next: (rep) => {
        this.report.set(rep);
        this.reportLoading.set(false);
      },
      error: () => {
        this.report.set(null);
        this.reportLoading.set(false);
      }
    });
  }

  deleteConfig(id: string): void {
    this.deletingId.set(id);
    this.api.delete(id).subscribe({
      next: () => {
        this.deletingId.set(null);
        this.refresh();
      },
      error: () => {
        this.deletingId.set(null);
      }
    });
  }

  private tryExtractConfigId(text: string): string | null {
    try {
      const parsed = JSON.parse(text);
      if (parsed && typeof parsed.configId === 'string') {
        return parsed.configId;
      }
    } catch {
      // ignore parse errors here; upload API will handle invalid JSON
    }
    return null;
  }

  private ensureNotDuplicate(): boolean {
    if (!this.selectedConfigId) {
      this.uploadError.set(null);
      return false;
    }
    const exists = this.configs().some((cfg) => cfg.configId === this.selectedConfigId);
    if (exists) {
      this.uploadError.set(`Config ${this.selectedConfigId} already exists`);
      this.uploadFile = undefined;
      return true;
    }
    this.uploadError.set(null);
    return false;
  }
}

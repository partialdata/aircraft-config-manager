import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { NgFor, NgIf, DatePipe, KeyValuePipe, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfigStore } from './config.store';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, KeyValuePipe, JsonPipe, FormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
<div class="layout">
  <header class="hero card">
    <div>
      <p class="eyebrow">Local microservices demo</p>
      <h1>{{ title }}</h1>
      <p class="subhead">Upload aircraft configuration JSON, validate through Java + Python services, and compare versions.</p>
      <div class="actions">
        <button [disabled]="store.listLoading()" (click)="store.refresh()">Refresh</button>
      </div>
    </div>
    <div class="hero-stats">
      <div class="metric card">Configs: <strong>{{ store.configs().length }}</strong></div>
      <div class="metric card">Analyzer: <span class="badge success">live</span></div>
    </div>
  </header>

  <section class="grid">
    <div class="card">
      <h3>Upload configuration</h3>
      <p class="muted">Upload a .json file or paste raw JSON.</p>
      <input type="file" accept=".json" (change)="onFileChange($event)" />
      <p class="muted center">or</p>
      <textarea rows="8" placeholder="{ ... }" [ngModel]="store.uploadJson()" (ngModelChange)="store.setUploadJson($event)"></textarea>
      <button [disabled]="store.uploadLoading()" (click)="store.upload()">
        {{ store.uploadLoading() ? 'Uploading...' : 'Submit' }}
      </button>
      <p *ngIf="store.uploadError()" class="error">{{ store.uploadError() }}</p>
      <div *ngIf="store.uploadResult() as uploadResult" class="result">
        <p>Saved as <strong>{{ uploadResult.id }}</strong></p>
        <div class="pill-row">
          <span class="badge" [ngClass]="uploadResult.validation.errors.length ? 'error' : 'success'">Java errors: {{ uploadResult.validation.errors.length }}</span>
          <span class="badge warning">Warnings: {{ uploadResult.validation.warnings.length + uploadResult.analyzer.warnings.length }}</span>
        </div>
      </div>
    </div>

    <div class="card">
      <h3>Compare configurations</h3>
      <div class="pill-row">
        <div class="select-group">
          <label class="muted">Config A</label>
          <select [ngModel]="store.firstId()" (ngModelChange)="store.setFirstId($event)">
            <option [ngValue]="''">Select config</option>
            <option *ngFor="let cfg of store.configs()" [ngValue]="cfg.id">{{ cfg.configId || cfg.id }} · {{ cfg.navDataCycle }}</option>
          </select>
        </div>
        <div class="select-group">
          <label class="muted">Config B</label>
          <select [ngModel]="store.secondId()" (ngModelChange)="store.setSecondId($event)">
            <option [ngValue]="''">Select config</option>
            <option *ngFor="let cfg of store.configs()" [ngValue]="cfg.id">{{ cfg.configId || cfg.id }} · {{ cfg.navDataCycle }}</option>
          </select>
        </div>
        <button [disabled]="!store.canCompare()" (click)="store.runCompare()">
          {{ store.compareLoading() ? 'Comparing...' : 'Compare' }}
        </button>
      </div>
      <p class="muted">Select any two stored configs to view diffs.</p>
    </div>
  </section>

  <section class="card table-wrap">
    <h3>Stored configurations</h3>
    <p *ngIf="store.listError()" class="error">{{ store.listError() }}</p>
    <div class="table-scroll">
      <table class="table">
        <thead>
          <tr><th>ID</th><th>Aircraft</th><th>Version</th><th>NAV</th><th>Created</th><th class="actions-col">Actions</th></tr>
        </thead>
        <tbody>
          <tr *ngFor="let cfg of store.configs()">
            <td>{{ cfg.configId || cfg.id }}</td>
            <td>{{ cfg.aircraftType }}</td>
            <td>{{ cfg.softwareVersion }}</td>
            <td>{{ cfg.navDataCycle }}</td>
            <td>{{ cfg.createdAt | date:'short' }}</td>
            <td class="pill-row actions-col">
              <button [disabled]="store.reportLoading()" (click)="store.loadReport(cfg.id)">
                {{ store.reportLoading() ? 'Loading...' : 'Report' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>

  <section class="grid">
    <div class="card" *ngIf="store.compareResult() as compareResult">
      <h3>Diff</h3>
      <p class="muted">{{ compareResult.firstId }} → {{ compareResult.secondId }}</p>
      <div *ngFor="let key of compareResult.changedFields | keyvalue">
        <strong>{{ key.key }}</strong>: {{ key.value }}
      </div>
      <div *ngIf="compareResult.addedModules.length">
        <p class="diff-add">Added modules: {{ compareResult.addedModules.join(', ') }}</p>
      </div>
      <div *ngIf="compareResult.removedModules.length">
        <p class="diff-remove">Removed modules: {{ compareResult.removedModules.join(', ') }}</p>
      </div>
    </div>

    <div class="card" *ngIf="store.report() as report">
      <h3>Report</h3>
      <p class="muted">Generated {{ report.generatedAt | date:'short' }}</p>
      <div class="pill-row">
        <span class="badge success">Java OK: {{ report.validation.errors.length === 0 }}</span>
        <span class="badge warning">Warnings: {{ report.validation.warnings.length + report.analyzer.warnings.length }}</span>
      </div>
      <h4>Metadata</h4>
      <pre>{{ report.metadata | json }}</pre>
      <h4>Validation</h4>
      <pre>{{ report.validation | json }}</pre>
      <h4>Analyzer</h4>
      <pre>{{ report.analyzer | json }}</pre>
    </div>
  </section>
</div>
  `,
  styles: []
})
export class AppComponent {
  title = 'Aircraft Config Manager';
  readonly store = inject(ConfigStore);

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.store.setUploadFile(target.files[0]);
    }
  }
}

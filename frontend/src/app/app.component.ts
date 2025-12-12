import { Component, OnInit } from '@angular/core';
import { NgFor, NgIf, DatePipe, KeyValuePipe, JsonPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfigService } from './config.service';
import { ConfigSummary, DiffResponse, ReportResponse, UploadResponse } from './types';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, KeyValuePipe, JsonPipe, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Aircraft Config Manager';
  configs: ConfigSummary[] = [];
  uploadJson = '';
  uploadFile?: File;
  uploadResult?: UploadResponse;
  loading = false;
  compareResult?: DiffResponse;
  report?: ReportResponse;
  firstId = '';
  secondId = '';
  error?: string;

  constructor(private api: ConfigService) {}

  ngOnInit() {
    this.refresh();
  }

  refresh() {
    this.api.listConfigs().subscribe(data => this.configs = data);
  }

  onFileChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.uploadFile = target.files[0];
    }
  }

  upload() {
    this.loading = true;
    this.error = undefined;
    this.api.upload(this.uploadFile, this.uploadJson).subscribe({
      next: (res) => {
        this.uploadResult = res;
        this.loading = false;
        this.refresh();
      },
      error: (err) => {
        this.error = err?.error?.message || 'Upload failed';
        this.uploadResult = {
          id: '',
          message: this.error,
          validation: err?.error?.validation ?? { warnings: [], errors: [] },
          analyzer: err?.error?.analyzer ?? { warnings: [], errors: [] }
        };
        this.loading = false;
      }
    });
  }

  selectForCompare(id: string, position: 'first' | 'second') {
    if (position === 'first') {
      this.firstId = id;
    } else {
      this.secondId = id;
    }
  }

  runCompare() {
    if (!this.firstId || !this.secondId) return;
    this.api.compare(this.firstId, this.secondId).subscribe(diff => this.compareResult = diff);
  }

  loadReport(id: string) {
    this.api.report(id).subscribe(rep => this.report = rep);
  }
}

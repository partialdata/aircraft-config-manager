import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ConfigSummary, DiffResponse, ReportResponse, UploadResponse } from './types';

@Injectable({ providedIn: 'root' })
export class ConfigService {
  private baseUrl = '/api/configs';

  constructor(private http: HttpClient) {}

  listConfigs(): Observable<ConfigSummary[]> {
    return this.http.get<ConfigSummary[]>(this.baseUrl);
  }

  upload(file?: File, configJson?: string): Observable<UploadResponse> {
    const form = new FormData();
    if (file) form.append('file', file);
    if (configJson) form.append('configJson', configJson);
    return this.http.post<UploadResponse>(this.baseUrl, form);
  }

  compare(firstId: string, secondId: string): Observable<DiffResponse> {
    return this.http.post<DiffResponse>(`${this.baseUrl}/compare`, { firstId, secondId });
  }

  report(id: string): Observable<ReportResponse> {
    return this.http.get<ReportResponse>(`${this.baseUrl}/${id}/report`);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}

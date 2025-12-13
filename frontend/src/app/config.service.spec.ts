import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ConfigService } from './config.service';

const BASE = '/api/configs';

describe('ConfigService', () => {
  let service: ConfigService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ConfigService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    http.verify();
  });

  it('lists configs', () => {
    const mock = [{ id: '1', configId: 'ACM-1001', aircraftType: 'A320', softwareVersion: '1.2.3', navDataCycle: 'AIRAC-2024-08', createdAt: new Date().toISOString() }];

    service.listConfigs().subscribe((resp) => {
      expect(resp).toEqual(mock);
    });

    const req = http.expectOne(BASE);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('uploads with file form data when provided', () => {
    const file = new File(['{}'], 'config.json', { type: 'application/json' });
    service.upload(file, undefined).subscribe();

    const req = http.expectOne(BASE);
    expect(req.request.method).toBe('POST');
    expect(req.request.body instanceof FormData).toBe(true);
    expect((req.request.body as FormData).has('file')).toBe(true);
    req.flush({});
  });

  it('compares two configs', () => {
    service.compare('a', 'b').subscribe();
    const req = http.expectOne(`${BASE}/compare`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ firstId: 'a', secondId: 'b' });
    req.flush({});
  });

  it('fetches report', () => {
    service.report('abc').subscribe();
    const req = http.expectOne(`${BASE}/abc/report`);
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('deletes a config', () => {
    service.delete('abc').subscribe();
    const req = http.expectOne(`${BASE}/abc`);
    expect(req.request.method).toBe('DELETE');
    req.flush({});
  });
});

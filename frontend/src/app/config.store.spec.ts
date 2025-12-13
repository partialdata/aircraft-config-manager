import { fakeAsync, flush, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { ConfigService } from './config.service';
import { ConfigStore } from './config.store';
import { ConfigSummary, DiffResponse, ReportResponse, UploadResponse } from './types';

class ConfigServiceStub {
  listConfigs = jasmine.createSpy('listConfigs').and.returnValue(of([] as ConfigSummary[]));
  upload = jasmine.createSpy('upload').and.returnValue(of({} as UploadResponse));
  compare = jasmine.createSpy('compare').and.returnValue(of({} as DiffResponse));
  report = jasmine.createSpy('report').and.returnValue(of({} as ReportResponse));
}

describe('ConfigStore', () => {
  let store: ConfigStore;
  let api: ConfigServiceStub;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: ConfigService, useClass: ConfigServiceStub }, ConfigStore]
    });

    store = TestBed.inject(ConfigStore);
    api = TestBed.inject(ConfigService) as unknown as ConfigServiceStub;
  });

  it('loads configs through the signal bridge', fakeAsync(() => {
    store.configs(); // ensure subscription activates
    flush();
    expect(api.listConfigs).toHaveBeenCalledTimes(1);
    const next = [{ id: '1', configId: 'A', aircraftType: 'A320', softwareVersion: '1.0', navDataCycle: '2024-01', createdAt: new Date().toISOString() }];
    api.listConfigs.and.returnValue(of(next));
    store.refresh();
    flush();
    expect(api.listConfigs).toHaveBeenCalledTimes(2);
    expect(store.configs()).toEqual(next);
  }));

  it('handles successful upload and triggers a refresh', () => {
    const res: UploadResponse = {
      id: '123',
      message: 'ok',
      validation: { warnings: [], errors: [] },
      analyzer: { warnings: [], errors: [] }
    };
    api.upload.and.returnValue(of(res));
    spyOn(store, 'refresh');

    store.setUploadJson('{ }');
    store.upload();

    expect(api.upload).toHaveBeenCalled();
    expect(store.uploadResult()).toEqual(res);
    expect(store.uploadLoading()).toBeFalse();
    expect(store.refresh).toHaveBeenCalled();
  });

  it('maps upload errors into state', () => {
    api.upload.and.returnValue(
      throwError(() => ({ error: { message: 'bad', validation: { warnings: [], errors: ['x'] } } }))
    );
    store.upload();
    expect(store.uploadError()).toBe('bad');
    expect(store.uploadResult()?.validation.errors).toContain('x');
  });

  it('runs compare when ids are set', () => {
    const diff: DiffResponse = { firstId: 'one', secondId: 'two', changedFields: {}, addedModules: [], removedModules: [] };
    api.compare.and.returnValue(of(diff));
    store.setFirstId('one');
    store.setSecondId('two');
    store.runCompare();
    expect(store.compareResult()).toEqual(diff);
  });

  it('loads report results', () => {
    const report: ReportResponse = {
      id: 'one',
      metadata: {},
      validation: { warnings: [], errors: [] },
      analyzer: { warnings: [], errors: [] },
      generatedAt: new Date().toISOString()
    };
    api.report.and.returnValue(of(report));
    store.loadReport('one');
    expect(store.report()).toEqual(report);
  });
});

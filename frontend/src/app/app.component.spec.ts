import { ComponentFixture, TestBed } from '@angular/core/testing';
import { signal } from '@angular/core';
import { By } from '@angular/platform-browser';
import { AppComponent } from './app.component';
import { ConfigStore } from './config.store';
import { DiffResponse, UploadResponse } from './types';

class ConfigStoreStub {
  configs = signal([]);
  listLoading = signal(false);
  listError = signal<string | null>(null);
  uploadJson = signal('');
  uploadResult = signal<UploadResponse | null>(null);
  uploadLoading = signal(false);
  uploadError = signal<string | null>(null);
  uploadDisabled = signal(false);
  compareResult = signal<DiffResponse | null>(null);
  compareLoading = signal(false);
  report = signal(null);
  reportLoading = signal(false);
  firstId = signal('');
  secondId = signal('');
  canCompare = signal(true);
  deletingId = signal<string | null>(null);

  refresh = jasmine.createSpy('refresh');
  setUploadJson = jasmine.createSpy('setUploadJson');
  setUploadFile = jasmine.createSpy('setUploadFile');
  upload = jasmine.createSpy('upload');
  setFirstId = jasmine.createSpy('setFirstId');
  setSecondId = jasmine.createSpy('setSecondId');
  runCompare = jasmine.createSpy('runCompare');
  loadReport = jasmine.createSpy('loadReport');
  deleteConfig = jasmine.createSpy('deleteConfig');
}

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let store: ConfigStoreStub;

  beforeEach(async () => {
    store = new ConfigStoreStub();
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [{ provide: ConfigStore, useValue: store }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
  });

  it('wires refresh button to the store', () => {
    fixture.detectChanges();
    const refreshBtn = fixture.debugElement.query(By.css('button'));
    refreshBtn.triggerEventHandler('click', {});
    expect(store.refresh).toHaveBeenCalled();
  });

  it('sends file selection into the store', () => {
    const file = new File(['{}'], 'config.json', { type: 'application/json' });
    const input = fixture.debugElement.query(By.css('input[type="file"]'));
    const event = { target: { files: [file] } };
    input.triggerEventHandler('change', event);
    expect(store.setUploadFile).toHaveBeenCalledWith(file);
  });

  it('fires upload through the facade', () => {
    fixture.detectChanges();
    const uploadBtn = fixture.debugElement.queryAll(By.css('button')).find(btn =>
      (btn.nativeElement.textContent as string).includes('Submit')
    );
    uploadBtn?.triggerEventHandler('click', {});
    expect(store.upload).toHaveBeenCalled();
  });

  it('fires delete through the facade', () => {
    store.configs.set([{ id: 'one', configId: 'A', aircraftType: '', softwareVersion: '', navDataCycle: '', createdAt: '' }]);
    fixture.detectChanges();
    const deleteBtn = fixture.debugElement.queryAll(By.css('button')).find(btn =>
      (btn.nativeElement.textContent as string).includes('Delete')
    );
    deleteBtn?.triggerEventHandler('click', {});
    expect(store.deleteConfig).toHaveBeenCalledWith('one');
  });
});

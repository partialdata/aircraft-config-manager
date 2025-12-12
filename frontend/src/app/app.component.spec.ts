import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AppComponent } from './app.component';
import { ConfigService } from './config.service';
import { UploadResponse } from './types';

class ConfigServiceStub {
  listConfigs = jasmine.createSpy('listConfigs').and.returnValue(of([]));
  upload = jasmine.createSpy('upload').and.returnValue(of({} as UploadResponse));
  compare = jasmine.createSpy('compare').and.returnValue(of({} as any));
  report = jasmine.createSpy('report').and.returnValue(of({} as any));
}

describe('AppComponent', () => {
  let fixture: ComponentFixture<AppComponent>;
  let component: AppComponent;
  let api: ConfigServiceStub;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [{ provide: ConfigService, useClass: ConfigServiceStub }]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    api = TestBed.inject(ConfigService) as unknown as ConfigServiceStub;
  });

  it('loads configs on init', () => {
    fixture.detectChanges();
    expect(api.listConfigs).toHaveBeenCalled();
  });

  it('handles successful upload', () => {
    const res = { id: '123', message: 'ok', validation: { warnings: [], errors: [] }, analyzer: { warnings: [], errors: [] } } as UploadResponse;
    api.upload.and.returnValue(of(res));
    component.uploadJson = '{ }';
    component.upload();
    expect(api.upload).toHaveBeenCalled();
    expect(component.uploadResult).toEqual(res);
    expect(component.error).toBeUndefined();
  });

  it('maps upload errors safely', () => {
    api.upload.and.returnValue(throwError(() => ({ error: { message: 'bad', validation: { warnings: [], errors: ['x'] } } })));
    component.upload();
    expect(component.error).toBe('bad');
    expect(component.uploadResult?.validation?.errors).toContain('x');
  });

  it('triggers compare when both ids are set', () => {
    component.firstId = 'one';
    component.secondId = 'two';
    component.runCompare();
    expect(api.compare).toHaveBeenCalledWith('one', 'two');
  });
});

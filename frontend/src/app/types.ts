export interface ConfigSummary {
  id: string;
  configId: string;
  aircraftType: string;
  softwareVersion: string;
  navDataCycle: string;
  createdAt: string;
}

export interface ValidationResult {
  warnings: string[];
  errors: string[];
}

export interface UploadResponse {
  id: string;
  message: string;
  validation: ValidationResult;
  analyzer: ValidationResult;
}

export interface DiffResponse {
  firstId: string;
  secondId: string;
  changedFields: Record<string, string>;
  addedModules: string[];
  removedModules: string[];
}

export interface ReportResponse {
  id: string;
  metadata: Record<string, any>;
  validation: ValidationResult;
  analyzer: ValidationResult;
  generatedAt: string;
}

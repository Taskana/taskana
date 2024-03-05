import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { StartupService } from 'app/shared/services/startup/startup.service';

@Injectable({
  providedIn: 'root'
})
export class RoutingUploadService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/routing-rules/default';
  }

  uploadRoutingRules(file: File) {
    const formData = new FormData();
    formData.append('excelRoutingFile', file);
    const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');
    return this.httpClient.put(this.url, formData, { headers });
  }
}

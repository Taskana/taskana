import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { StartupService } from '../shared/services/startup/startup.service';

@Injectable({
  providedIn: 'root'
})
export class RoutingUploadService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/dmn-upload/';
  }

  uploadRoutingRules(file: File) {
    const formData = new FormData();
    formData.append('excelRoutingFile', file);
    const headers = new HttpHeaders().set('Content-Type', 'multipart/form-data');
    return this.httpClient.post(this.url, formData, { headers });
  }
}

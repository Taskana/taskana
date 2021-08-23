import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { StartupService } from '../shared/services/startup/startup.service';

@Injectable({
  providedIn: 'root'
})
export class RoutingUploadService {
  constructor(private httpClient: HttpClient, private startupService: StartupService) {}

  get url(): string {
    return this.startupService.getTaskanaRestUrl() + '/v1/routing-upload/';
  }
}

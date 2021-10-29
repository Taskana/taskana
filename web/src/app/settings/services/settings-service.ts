import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Settings } from '../models/settings';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class SettingsService {
  constructor(private httpClient: HttpClient) {}

  // GET
  getSettings(): Observable<Settings> {
    return this.httpClient
      .get<SettingsRepresentation>(`${environment.taskanaRestUrl}/v1/config/custom-attributes`)
      .pipe(map((b) => b.customAttributes));
  }

  // PUT
  updateSettings(settings: Settings) {
    return this.httpClient.put<Settings>(`${environment.taskanaRestUrl}/v1/config/custom-attributes`, {
      customAttributes: settings
    });
  }
}

interface SettingsRepresentation {
  customAttributes: Settings;
}

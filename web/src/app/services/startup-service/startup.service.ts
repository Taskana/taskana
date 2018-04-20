import { Observable } from 'rxjs/Observable';
import { HttpClient } from '@angular/common/http';
import { CanActivate } from '@angular/router';
import { environment } from 'app/../environments/environment';
import { Injectable } from '@angular/core';

@Injectable()
export class StartupService {
    constructor(private httpClient: HttpClient) { }
    load(): Promise<any> {
        return this.httpClient.get<any>('environments/data-sources/environment-information.json').map(jsonFile => {
            if (jsonFile) {
                environment.taskanaWorkplaceUrl = jsonFile.taskanaWorkplaceUrl === '' ?
                    environment.taskanaWorkplaceUrl : jsonFile.taskanaWorkplaceUrl;
                environment.taskanaAdminUrl = jsonFile.taskanaAdminUrl === '' ?
                    environment.taskanaAdminUrl : jsonFile.taskanaAdminUrl;
                environment.taskanaMonitorUrl = jsonFile.taskanaMonitorUrl === '' ?
                    environment.taskanaMonitorUrl : jsonFile.taskanaMonitorUrl;
                environment.taskanaRestUrl = jsonFile.taskanaRestUrl === '' ?
                    environment.taskanaRestUrl : jsonFile.taskanaRestUrl;
            }
        }).toPromise()
            .catch(() => {
                return Observable.of(true)
            })
    }
}

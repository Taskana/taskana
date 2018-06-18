import { Observable } from 'rxjs/Observable';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'app/../environments/environment';
import { Injectable, Inject, Injector } from '@angular/core';
import { TitlesService } from 'app/services/titles/titles.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';

@Injectable()
export class StartupService {


    constructor(
        private httpClient: HttpClient,
        private titlesService: TitlesService,
        private customFieldsService: CustomFieldsService,
        private taskanaEngineService: TaskanaEngineService,
        private injector: Injector) { }

    load(): Promise<any> {
        return this.loadEnvironment();
    }

    private loadEnvironment() {
        return this.getEnvironmentFilePromise().then(
            () => this.geCustomizedFieldsFilePromise()
        ).then(
            () => this.taskanaEngineService.getUserInformation()
            ).catch(error => {
                this.router.navigate(['no-role']);
            });
    }

    getEnvironmentFilePromise() {
        return this.httpClient.get<any>('environments/data-sources/environment-information.json').map(jsonFile => {
            if (jsonFile) {
                environment.taskanaRestUrl = jsonFile.taskanaRestUrl === '' ?
                    environment.taskanaRestUrl : jsonFile.taskanaRestUrl;
                this.customFieldsService.initCustomFields('EN', jsonFile);
            }
        }).toPromise()
            .catch(() => {
                return Observable.of(true)
            });
    }

    geCustomizedFieldsFilePromise() {
        return this.httpClient.get<any>('environments/data-sources/customized-fields.json').map(jsonFile => {
            if (jsonFile) {
                this.customFieldsService.initCustomFields('EN', jsonFile);
            }
        }).toPromise()
            .catch(() => {
                return Observable.of(true)
            });
    }

    public get router(): Router {
        return this.injector.get(Router);
    }
}

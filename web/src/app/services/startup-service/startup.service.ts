import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'app/../environments/environment';
import { Injectable, Injector } from '@angular/core';
import { TitlesService } from 'app/services/titles/titles.service';
import { CustomFieldsService } from 'app/services/custom-fields/custom-fields.service';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { map } from 'rxjs/operators';
import { WindowRefService } from 'app/services/window/window.service';

@Injectable()
export class StartupService {


    constructor(
        private httpClient: HttpClient,
        private titlesService: TitlesService,
        private customFieldsService: CustomFieldsService,
        private taskanaEngineService: TaskanaEngineService,
        private injector: Injector,
        private window: WindowRefService) { }

    load(): Promise<any> {
        return this.loadEnvironment();
    }

    private loadEnvironment() {
        return this.getEnvironmentFilePromise().then(
            () => this.geCustomizedFieldsFilePromise()
        ).then(
            () => this.taskanaEngineService.getUserInformation()
        ).catch(error => {
            this.window.nativeWindow.location.href = environment.taskanaRestUrl + '/login';
        });
    }

    getEnvironmentFilePromise() {
        return this.httpClient.get<any>('environments/data-sources/environment-information.json').pipe(map(jsonFile => {
            if (jsonFile) {
                environment.taskanaRestUrl = jsonFile.taskanaRestUrl === '' ?
                    environment.taskanaRestUrl : jsonFile.taskanaRestUrl;
                this.customFieldsService.initCustomFields('EN', jsonFile);
            }
        })).toPromise()
            .catch(() => {
                return of(true)
            });
    }

    geCustomizedFieldsFilePromise() {
        return this.httpClient.get<any>('environments/data-sources/taskana-customization.json').pipe(map(jsonFile => {
            if (jsonFile) {
                this.customFieldsService.initCustomFields('EN', jsonFile);
            }
        })).toPromise()
            .catch(() => {
                return of(true)
            });
    }

    public get router(): Router {
        return this.injector.get(Router);
    }
}

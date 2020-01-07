import {of} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {environment} from 'app/../environments/environment';
import {Injectable, Injector} from '@angular/core';
import {CustomFieldsService} from 'app/services/custom-fields/custom-fields.service';
import {TaskanaEngineService} from 'app/services/taskana-engine/taskana-engine.service';
import {map} from 'rxjs/operators';
import {WindowRefService} from 'app/services/window/window.service';

@Injectable()
export class StartupService {
  constructor(
    private httpClient: HttpClient,
    private customFieldsService: CustomFieldsService,
    private taskanaEngineService: TaskanaEngineService,
    private injector: Injector,
    private window: WindowRefService) {
  }

  public get router(): Router {
    return this.injector.get(Router);
  }

  load(): Promise<any> {
    return this.loadEnvironment();
  }

  getEnvironmentFilePromise() {
    return this.httpClient.get<any>('environments/data-sources/environment-information.json').pipe(map(jsonFile => {
      if (jsonFile && jsonFile.taskanaRestUrl) {
        environment.taskanaRestUrl = jsonFile.taskanaRestUrl;
      }

      if (jsonFile && jsonFile.taskanaLogoutUrl) {
        environment.taskanaLogoutUrl = jsonFile.taskanaLogoutUrl;
      }
      this.customFieldsService.initCustomFields('EN', jsonFile);
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

  private loadEnvironment() {
    return this.getEnvironmentFilePromise().then(
      () => this.geCustomizedFieldsFilePromise()
    ).then(
      () => this.taskanaEngineService.getUserInformation()
    ).catch(error => {
      console.log(error);
      //this.window.nativeWindow.location.href = environment.taskanaRestUrl + '/login';
    });
  }
}

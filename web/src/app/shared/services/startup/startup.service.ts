import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'app/../environments/environment';
import { Injectable, Injector } from '@angular/core';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';
import { map } from 'rxjs/operators';
import { WindowRefService } from 'app/shared/services/window/window.service';

@Injectable()
export class StartupService {
  constructor(
    private httpClient: HttpClient,
    private taskanaEngineService: TaskanaEngineService,
    private injector: Injector,
    private window: WindowRefService
  ) {}

  public get router(): Router {
    return this.injector.get(Router);
  }

  load(): Promise<any> {
    return this.loadEnvironment();
  }

  // TODO: refactor this
  getEnvironmentFilePromise() {
    return this.httpClient
      .get<any>('environments/data-sources/environment-information.json')
      .pipe(
        map((jsonFile) => {
          if (jsonFile && jsonFile.taskanaRestUrl) {
            environment.taskanaRestUrl = jsonFile.taskanaRestUrl;
          }

          if (jsonFile && jsonFile.taskanaLogoutUrl) {
            environment.taskanaLogoutUrl = jsonFile.taskanaLogoutUrl;
          }
        })
      )
      .toPromise()
      .catch(() => of(true));
  }

  getTaskanaRestUrl() {
    return environment.taskanaRestUrl;
  }

  getTaskanaLogoutUrl() {
    return environment.taskanaLogoutUrl;
  }

  private loadEnvironment() {
    return this.getEnvironmentFilePromise()
      .then(() => this.taskanaEngineService.getUserInformation())
      .catch((error) => {
        // this.window.nativeWindow.location.href = environment.taskanaRestUrl + '/login';
      });
  }
}

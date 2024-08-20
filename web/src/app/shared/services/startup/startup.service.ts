import { of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { environment } from 'app/../environments/environment';
import { Injectable, Injector } from '@angular/core';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';
import { map } from 'rxjs/operators';
import { WindowRefService } from 'app/shared/services/window/window.service';

@Injectable()
export class StartupService {
  constructor(
    private httpClient: HttpClient,
    private kadaiEngineService: KadaiEngineService,
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
          if (jsonFile && jsonFile.kadaiRestUrl) {
            environment.kadaiRestUrl = jsonFile.kadaiRestUrl;
          }

          if (jsonFile && jsonFile.kadaiLogoutUrl) {
            environment.kadaiLogoutUrl = jsonFile.kadaiLogoutUrl;
          }
        })
      )
      .toPromise()
      .catch(() => of(true));
  }

  getKadaiRestUrl() {
    return environment.kadaiRestUrl;
  }

  getKadaiLogoutUrl() {
    return environment.kadaiLogoutUrl;
  }

  private loadEnvironment() {
    return this.getEnvironmentFilePromise()
      .then(() => this.kadaiEngineService.getUserInformation())
      .catch((error) => {
        // this.window.nativeWindow.location.href = environment.kadaiRestUrl + '/login';
      });
  }
}

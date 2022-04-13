import { CanActivate, Router, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';

@Injectable()
export class MonitorGuard implements CanActivate {
  static roles = ['ADMIN', 'MONITOR'];

  constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.taskanaEngineService.hasRole(MonitorGuard.roles)) {
      return true;
    }

    return this.router.parseUrl('/taskana/workplace');
  }
}

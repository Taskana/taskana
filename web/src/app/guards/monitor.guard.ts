import { CanActivate, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';

@Injectable()
export class MonitorGuard implements CanActivate {
  static roles = ['ADMIN', 'MONITOR'];
  constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) { }

  canActivate() {
    if (this.taskanaEngineService.hasRole(MonitorGuard.roles)) {
      return true;
    }
    return this.navigateToWorkplace();
  }

  navigateToWorkplace(): boolean {
    this.router.navigate(['workplace']);
    return false;
  }
}

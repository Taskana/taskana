import { CanActivate, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';

@Injectable()
export class UserGuard implements CanActivate {
  static roles = ['ADMIN', 'USER'];
  constructor(private taskanaEngineService: TaskanaEngineService, private router: Router) { }
  canActivate() {
    if (this.taskanaEngineService.hasRole(UserGuard.roles)) {
      return true;
    }
    return this.navigateToNoRole();
  }

  navigateToNoRole(): boolean {
    this.router.navigate(['no-role']);
    return false;
  }
}

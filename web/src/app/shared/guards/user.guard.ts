import { CanActivate, Router, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { TaskanaEngineService } from 'app/shared/services/taskana-engine/taskana-engine.service';

@Injectable()
export class UserGuard implements CanActivate {
  static roles = ['ADMIN', 'USER'];

  constructor(private taskanaEngineService: TaskanaEngineService, private router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.taskanaEngineService.hasRole(UserGuard.roles)) {
      return true;
    }

    return this.router.parseUrl('/taskana/no-role');
  }
}

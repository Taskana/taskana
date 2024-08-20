import { CanActivate, Router, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';

@Injectable()
export class UserGuard implements CanActivate {
  static roles = ['ADMIN', 'USER'];

  constructor(private kadaiEngineService: KadaiEngineService, private router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.kadaiEngineService.hasRole(UserGuard.roles)) {
      return true;
    }

    return this.router.parseUrl('/kadai/no-role');
  }
}

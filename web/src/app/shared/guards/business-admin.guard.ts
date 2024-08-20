import { CanActivate, Router, UrlTree } from '@angular/router';
import { Injectable } from '@angular/core';
import { KadaiEngineService } from 'app/shared/services/kadai-engine/kadai-engine.service';

@Injectable()
export class BusinessAdminGuard implements CanActivate {
  static roles = ['ADMIN', 'BUSINESS_ADMIN'];

  constructor(private kadaiEngineService: KadaiEngineService, public router: Router) {}

  canActivate(): boolean | UrlTree {
    if (this.kadaiEngineService.hasRole(BusinessAdminGuard.roles)) {
      return true;
    }

    return this.router.parseUrl('/kadai/workplace');
  }
}

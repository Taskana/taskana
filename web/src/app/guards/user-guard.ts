import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CanActivate, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ErrorModel } from 'app/models/modal-error';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';
import { WindowRefService } from 'app/services/window/window.service';

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

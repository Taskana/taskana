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

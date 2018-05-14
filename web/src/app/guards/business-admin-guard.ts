import { Observable } from 'rxjs/Observable';
import { HttpClient } from '@angular/common/http';
import { CanActivate, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { DomainService } from 'app/services/domain/domain.service';
import { ErrorModalService } from 'app/services/errorModal/error-modal.service';
import { ErrorModel } from 'app/models/modal-error';
import { TaskanaEngineService } from 'app/services/taskana-engine/taskana-engine.service';

@Injectable()
export class BusinessAdminGuard implements CanActivate {
    constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) { }

    canActivate() {
        return this.taskanaEngineService.getUserInformation().map(userInfo => {
            if (userInfo.roles.length === 0) {
                return this.navigateToWorplace();
            }
            const adminRole = userInfo.roles.find(role => {
                if (role === 'BUSINESS_ADMIN' || role === 'ADMIN' ) {
                    return true;
                }
            });
            if (adminRole) {
                return true;
            }
            return this.navigateToWorplace();
        }).catch(() => {

            return Observable.of(this.navigateToWorplace())
        });
    }

    navigateToWorplace(): boolean {
        this.router.navigate(['workplace']);
        return false
    }
}

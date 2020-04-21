import { Component, OnInit } from '@angular/core';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { UserGuard } from 'app/shared/guards/user.guard';
import { Router } from '@angular/router';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';

@Component({
  selector: 'taskana-no-access',
  templateUrl: './no-access.component.html',
  styleUrls: ['./no-access.component.scss']
})
export class NoAccessComponent implements OnInit {
  showNoAccess = false;
  constructor(private taskanaEngineService: TaskanaEngineService, public router: Router) { }

  ngOnInit() {
    if (this.taskanaEngineService.hasRole(BusinessAdminGuard.roles)) {
      this.router.navigate(['administration']);
    } else if (this.taskanaEngineService.hasRole(MonitorGuard.roles)) {
      this.router.navigate(['monitor']);
    } else if (this.taskanaEngineService.hasRole(UserGuard.roles)) {
      this.router.navigate(['workplace']);
    } else {
      this.showNoAccess = true;
    }
  }
}

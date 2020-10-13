import { Component, OnInit } from '@angular/core';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { UserGuard } from 'app/shared/guards/user.guard';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
import { SidenavService } from '../../services/sidenav/sidenav.service';

@Component({
  selector: 'taskana-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.scss']
})
export class SidenavListComponent implements OnInit {
  toggle: boolean = false;

  adminUrl = 'taskana/administration';
  monitorUrl = 'taskana/monitor';
  workplaceUrl = 'taskana/workplace';
  historyUrl = 'taskana/history';
  accessUrl = 'taskana/administration/access-items-management';
  classificationUrl = 'taskana/administration/classifications';
  workbasketsUrl = 'taskana/administration/workbaskets';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;
  historyAccess = false;

  admin_url_list: any[];

  constructor(private taskanaEngineService: TaskanaEngineService, private sidenavService: SidenavService) {}

  ngOnInit() {
    this.administrationAccess = this.taskanaEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.taskanaEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.taskanaEngineService.hasRole(UserGuard.roles);
    this.taskanaEngineService.isHistoryProviderEnabled().subscribe((value) => {
      this.historyAccess = value;
    });
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    this.sidenavService.toggle_sidenav();
  }
}

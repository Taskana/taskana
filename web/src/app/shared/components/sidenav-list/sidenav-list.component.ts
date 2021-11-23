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

  monitorUrl = 'taskana/monitor';
  workplaceUrl = 'taskana/workplace';
  historyUrl = 'taskana/history';
  accessUrl = 'taskana/administration/access-items-management';
  routingUrl = 'taskana/administration/task-routing';
  classificationUrl = 'taskana/administration/classifications';
  workbasketsUrl = 'taskana/administration/workbaskets';
  administrationsUrl = 'taskana/administration/workbaskets';
  settingsURL = 'taskana/settings';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;
  historyAccess = false;
  routingAccess = false;
  settingsAccess = false;

  constructor(private taskanaEngineService: TaskanaEngineService, private sidenavService: SidenavService) {}

  ngOnInit() {
    this.administrationAccess = this.taskanaEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.taskanaEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.taskanaEngineService.hasRole(UserGuard.roles);
    this.taskanaEngineService.isHistoryProviderEnabled().subscribe((value) => {
      this.historyAccess = value;
    });
    this.taskanaEngineService.isCustomRoutingRulesEnabled().subscribe((value) => {
      this.routingAccess = value;
    });
    this.settingsAccess = this.administrationAccess;
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    this.sidenavService.toggleSidenav();
  }
}

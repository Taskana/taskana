import { Component, OnInit } from '@angular/core';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { UserGuard } from 'app/shared/guards/user.guard';
import { KadaiEngineService } from '../../services/kadai-engine/kadai-engine.service';
import { SidenavService } from '../../services/sidenav/sidenav.service';
import { RequestInProgressService } from '../../services/request-in-progress/request-in-progress.service';
import { Router } from '@angular/router';

@Component({
  selector: 'kadai-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.scss']
})
export class SidenavListComponent implements OnInit {
  toggle: boolean = false;

  monitorUrl = 'kadai/monitor';
  workplaceUrl = 'kadai/workplace';
  historyUrl = 'kadai/history';
  accessUrl = 'kadai/administration/access-items-management';
  routingUrl = 'kadai/administration/task-routing';
  classificationUrl = 'kadai/administration/classifications';
  workbasketsUrl = 'kadai/administration/workbaskets';
  administrationsUrl = 'kadai/administration/workbaskets';
  settingsURL = 'kadai/settings';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;
  historyAccess = false;
  routingAccess = false;
  settingsAccess = false;

  constructor(
    private kadaiEngineService: KadaiEngineService,
    private sidenavService: SidenavService,
    private requestInProgressService: RequestInProgressService,
    private router: Router
  ) {}

  ngOnInit() {
    this.administrationAccess = this.kadaiEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.kadaiEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.kadaiEngineService.hasRole(UserGuard.roles);
    this.kadaiEngineService.isHistoryProviderEnabled().subscribe((value) => {
      this.historyAccess = value;
    });
    this.kadaiEngineService.isCustomRoutingRulesEnabled().subscribe((value) => {
      this.routingAccess = value;
    });
    this.settingsAccess = this.administrationAccess;
  }

  toggleSidenav(target: string) {
    if (!this.router.url.includes(target)) this.requestInProgressService.setRequestInProgress(true);
    this.toggle = !this.toggle;
    this.sidenavService.toggleSidenav();
  }
}

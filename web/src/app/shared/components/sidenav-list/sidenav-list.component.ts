import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from 'environments/environment';
import { SelectedRouteService } from 'app/shared/services/selected-route/selected-route';
import { Subscription } from 'rxjs';
import { DomainService } from 'app/shared/services/domain/domain.service';
import { BusinessAdminGuard } from 'app/shared/guards/business-admin.guard';
import { MonitorGuard } from 'app/shared/guards/monitor.guard';
import { WindowRefService } from 'app/shared/services/window/window.service';
import { UserGuard } from 'app/shared/guards/user.guard';
import { expandRight } from 'app/shared/animations/expand.animation';
import { TaskanaEngineService } from '../../services/taskana-engine/taskana-engine.service';
import { SidenavService } from '../../services/sidenav/sidenav.service';
import { Side } from 'app/administration/components/workbasket-distribution-targets/workbasket-distribution-targets.component';
import { ConstantPool } from '@angular/compiler';

@Component({
  selector: 'taskana-sidenav-list',
  templateUrl: './sidenav-list.component.html',
  styleUrls: ['./sidenav-list.component.scss']
})
export class SidenavListComponent implements OnInit {
  selectedRoute = '';
  route: string;
  title = '';

  titleAdministration = 'Administration';
  titleWorkbaskets = 'Workbaskets';
  titleClassifications = 'Classifications';
  titleAccessItems = 'Access items';
  titleMonitor = 'Monitor';
  titleWorkplace = 'Workplace';
  titleHistory = 'History';
  showNavbar = false;
  domains: Array<string> = [];
  selectedDomain: string;
  version: string;
  toggle: boolean = false;

  adminUrl = 'taskana/administration';
  monitorUrl = 'taskana/monitor';
  workplaceUrl = 'taskana/workplace';
  historyUrl = 'taskana/history';

  administrationAccess = false;
  monitorAccess = false;
  workplaceAccess = false;
  historyAccess = false;

  selectedRouteSubscription: Subscription;
  getDomainsSubscription: Subscription;

  constructor(
    private selectedRouteService: SelectedRouteService,
    private domainService: DomainService,
    private taskanaEngineService: TaskanaEngineService,
    private window: WindowRefService,
    private sidenavService: SidenavService
  ) {}

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
    });
    this.getDomainsSubscription = this.domainService.getDomains().subscribe((domains) => {
      this.domains = domains;
    });

    this.domainService.getSelectedDomain().subscribe((domain) => {
      this.selectedDomain = domain;
    });

    this.taskanaEngineService.getVersion().subscribe((restVersion) => {
      this.version = restVersion.version;
    });

    this.administrationAccess = this.taskanaEngineService.hasRole(BusinessAdminGuard.roles);
    this.monitorAccess = this.taskanaEngineService.hasRole(MonitorGuard.roles);
    this.workplaceAccess = this.taskanaEngineService.hasRole(UserGuard.roles);

    this.taskanaEngineService.isHistoryProviderEnabled().subscribe((value) => {
      this.historyAccess = value;
    });
  }

  logout() {
    this.taskanaEngineService.logout().subscribe(() => {});
    this.window.nativeWindow.location.href = environment.taskanaLogoutUrl;
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    console.log(this.toggle);
    this.sidenavService.toggle();
  }

  ngOnDestroy(): void {
    if (this.selectedRouteSubscription) {
      this.selectedRouteSubscription.unsubscribe();
    }
    if (this.getDomainsSubscription) {
      this.getDomainsSubscription.unsubscribe();
    }
  }
}

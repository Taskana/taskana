import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
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
  selector: 'taskana-shared-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
  animations: [expandRight]
})
export class NavBarComponent implements OnInit, OnDestroy {
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
  innerWidth: any;
  showTitle: boolean = true;

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

  @HostListener('window:resize', ['$event'])
  onResize() {
    this.innerWidth = window.innerWidth;
    console.log(this.innerWidth);
    if (this.innerWidth < 800) {
      this.showTitle = false;
    } else {
      this.showTitle = true;
    }
  }

  ngOnInit() {
    this.selectedRouteSubscription = this.selectedRouteService.getSelectedRoute().subscribe((value: string) => {
      this.selectedRoute = value;
      this.setTitle(value);
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

    this.innerWidth = window.innerWidth;
  }

  toggleSidenav() {
    this.toggle = !this.toggle;
    console.log(this.toggle);
    this.sidenavService.toggle();
  }

  private setTitle(value: string = 'workbaskets') {
    if (value.indexOf('workbaskets') === 0) {
      this.title = this.titleWorkbaskets;
    } else if (value.indexOf('classifications') === 0) {
      this.title = this.titleClassifications;
    } else if (value.indexOf('monitor') === 0) {
      this.title = this.titleMonitor;
    } else if (value.indexOf('workplace') === 0) {
      this.title = this.titleWorkplace;
    } else if (value.indexOf('access-items') === 0) {
      this.title = this.titleAccessItems;
    } else if (value.indexOf('history') === 0) {
      this.title = this.titleHistory;
    }
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

import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject } from 'rxjs';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { takeUntil } from 'rxjs/operators';
import { KadaiEngineService } from '../../../shared/services/kadai-engine/kadai-engine.service';

@Component({
  selector: 'kadai-administration-overview',
  templateUrl: './administration-overview.component.html',
  styleUrls: ['./administration-overview.component.scss']
})
export class AdministrationOverviewComponent implements OnInit {
  @Input() selectedTab = '';
  domains: Array<string> = [];
  selectedDomain: string;

  destroy$ = new Subject<void>();
  url$: Observable<any>;
  routingAccess = false;

  constructor(
    private router: Router,
    private domainService: DomainService,
    private kadaiEngineService: KadaiEngineService
  ) {
    router.events.pipe(takeUntil(this.destroy$)).subscribe((e) => {
      const urlPaths = this.router.url.split('/');
      if (this.router.url.includes('detail')) {
        this.selectedTab = urlPaths[urlPaths.length - 2];
      } else {
        this.selectedTab = urlPaths[urlPaths.length - 1];
      }
    });
  }

  ngOnInit() {
    this.kadaiEngineService
      .isCustomRoutingRulesEnabled()
      .pipe(takeUntil(this.destroy$))
      .subscribe((value) => {
        this.routingAccess = value;
      });
    this.domainService
      .getDomains()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domains) => {
        this.domains = domains;
      });

    this.domainService
      .getSelectedDomain()
      .pipe(takeUntil(this.destroy$))
      .subscribe((domain) => {
        this.selectedDomain = domain;
      });
  }

  switchDomain(domain) {
    this.domainService.switchDomain(domain);
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}

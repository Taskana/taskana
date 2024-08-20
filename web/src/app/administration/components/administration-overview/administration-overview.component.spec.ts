import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AdministrationOverviewComponent } from './administration-overview.component';
import { DebugElement } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterTestingModule } from '@angular/router/testing';
import { DomainService } from '../../../shared/services/domain/domain.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { KadaiEngineService } from '../../../shared/services/kadai-engine/kadai-engine.service';

const domainServiceSpy: Partial<DomainService> = {
  getDomains: jest.fn().mockReturnValue(of(['domain a', 'domain b'])),
  getSelectedDomain: jest.fn().mockReturnValue(of('domain a')),
  switchDomain: jest.fn()
};

describe('AdministrationOverviewComponent', () => {
  let component: AdministrationOverviewComponent;
  let fixture: ComponentFixture<AdministrationOverviewComponent>;
  let debugElement: DebugElement;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [
        MatSelectModule,
        MatTabsModule,
        RouterTestingModule.withRoutes([]),
        HttpClientTestingModule,
        NoopAnimationsModule
      ],
      declarations: [AdministrationOverviewComponent],
      providers: [{ provide: DomainService, useValue: domainServiceSpy }, KadaiEngineService]
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create component', () => {
    expect(component).toBeTruthy();
  });

  it('should render 3 tabs in navbar', () => {
    const navbar = fixture.debugElement.nativeElement.getElementsByClassName('administration-overview__navbar-links');
    expect(navbar).toHaveLength(3);
  });

  it('should display current domain', () => {
    const domainElem = fixture.debugElement.nativeElement.querySelector('.administration-overview__domain');
    expect(domainElem).toBeTruthy();

    fixture.detectChanges();
    expect(domainElem.textContent).toMatch('domain a');
  });
});

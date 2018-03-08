import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed,  } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { NoAccessComponent } from '../noAccess/no-access.component';
import { WorkbasketInformationComponent } from './information/workbasket-information.component';
import { AccessItemsComponent } from './access-items/access-items.component';
import { DistributionTargetsComponent } from './distribution-targets/distribution-targets.component';
import { Workbasket } from 'app/model/workbasket';
import { Observable } from 'rxjs/Observable';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ICONTYPES, IconTypeComponent } from '../../shared/type-icon/icon-type.component';
import { MapValuesPipe } from '../../pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../pipes/remove-none-type';
import { SelectWorkBasketPipe } from '../../pipes/seleted-workbasket.pipe';
import { AlertComponent } from '../../shared/alert/alert.component';
import { GeneralMessageModalComponent } from '../../shared/general-message-modal/general-message-modal.component';
import { Links } from 'app/model/links';
import { WorkbasketAccessItems } from '../../model/workbasket-access-items';

import { WorkbasketService } from '../../services/workbasket.service';
import { MasterAndDetailService } from '../../services/master-and-detail.service';
import { PermissionService } from '../../services/permission.service';
import { AlertService } from '../../services/alert.service';

import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';
import { WorkbasketSummary } from '../../model/workbasketSummary';

@Component({
	selector: 'taskana-filter',
	template: ''
})
export class FilterComponent {

}


describe('WorkbasketDetailsComponent', () => {
  let component: WorkbasketDetailsComponent;
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement;
  let masterAndDetailService;
  let workbasketService;
  

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
      declarations: [WorkbasketDetailsComponent, NoAccessComponent, WorkbasketInformationComponent, SpinnerComponent, IconTypeComponent, MapValuesPipe, RemoveNoneTypePipe, AlertComponent, GeneralMessageModalComponent, AccessItemsComponent, DistributionTargetsComponent, FilterComponent, SelectWorkBasketPipe],
      providers: [WorkbasketService, MasterAndDetailService, PermissionService, AlertService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkbasketDetailsComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    fixture.detectChanges();
    masterAndDetailService = TestBed.get(MasterAndDetailService);
    workbasketService = TestBed.get(WorkbasketService);
    spyOn(masterAndDetailService, 'getShowDetail').and.callFake(() => { return Observable.of(true) })
    spyOn(workbasketService, 'getSelectedWorkBasket').and.callFake(() => { return Observable.of('id1') })
    spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => { return Observable.of(new Array<WorkbasketSummary>(new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Array<Links>(new Links('self', 'someurl'))))) })
    spyOn(workbasketService, 'getWorkBasket').and.callFake(() => { return Observable.of(new Workbasket('id1')) })
    spyOn(workbasketService, 'getWorkBasketAccessItems').and.callFake(() => { return Observable.of(new Array<WorkbasketAccessItems>()) })
    spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => { return Observable.of(new Array<WorkbasketSummary>()) })

  });

  afterEach(() => {
    document.body.removeChild(debugElement);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should has created app-no-access if workbasket is not defined', () => {
    expect(component.workbasket).toBeUndefined();
    expect(debugElement.querySelector('app-no-access')).toBeTruthy;
  });

  it('should has created workbasket-details if workbasket is defined and app-no-access should dissapear', () => {
    expect(component.workbasket).toBeUndefined();
    expect(debugElement.querySelector('app-no-access')).toBeTruthy;

    component.workbasket = new Workbasket('id1');
    fixture.detectChanges();

    expect(debugElement.querySelector('app-no-access')).toBeFalsy;
    expect(debugElement.querySelector('worbasket-details')).toBeTruthy;

  });

  it('should show back button with classes "visible-xs visible-sm hidden" when showDetail property is true', () => {

    component.workbasket = new Workbasket('id1');
    component.ngOnInit();
    fixture.detectChanges();
    expect(debugElement.querySelector('.visible-xs.visible-sm.hidden > a').textContent).toBe('Back');

  });

});

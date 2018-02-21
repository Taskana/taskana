import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { WorkbasketDetailsComponent } from './workbasket-details.component';
import { NoAccessComponent } from '../noAccess/no-access.component';
import { WorkbasketInformationComponent } from './information/workbasket-information.component';
import { Workbasket } from 'app/model/workbasket';
import { Observable } from 'rxjs/Observable';
import { SpinnerComponent } from '../../shared/spinner/spinner.component';
import { ICONTYPES, IconTypeComponent } from '../../shared/type-icon/icon-type.component';
import { MapValuesPipe } from '../../pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../pipes/remove-none-type';

import { WorkbasketService } from '../../services/workbasketservice.service';
import { MasterAndDetailService } from '../../services/master-and-detail.service';
import { PermissionService } from '../../services/permission.service';

import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

describe('WorkbasketDetailsComponent', () => {
  let component: WorkbasketDetailsComponent;
  let fixture: ComponentFixture<WorkbasketDetailsComponent>;
  let debugElement;
  let masterAndDetailService;
  let workbasketService;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports:[RouterTestingModule, FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
      declarations: [ WorkbasketDetailsComponent, NoAccessComponent, WorkbasketInformationComponent, SpinnerComponent, IconTypeComponent, MapValuesPipe, RemoveNoneTypePipe ],
      providers:[WorkbasketService, MasterAndDetailService, PermissionService]
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
    spyOn(masterAndDetailService, 'getShowDetail').and.returnValue(Observable.of(true));
    spyOn(workbasketService,'getSelectedWorkBasket').and.returnValue(Observable.of('id1'));
    spyOn(workbasketService,'getWorkBasket').and.returnValue(Observable.of(new Workbasket('id1',null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)));
  });

  afterEach(() =>{
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

    component.workbasket = new Workbasket(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    fixture.detectChanges();

    expect(debugElement.querySelector('app-no-access')).toBeFalsy;
    expect(debugElement.querySelector('worbasket-details')).toBeTruthy;

  });

  it('should show back button with classes "visible-xs visible-sm hidden" when showDetail property is true', () => {
    
    component.workbasket = new Workbasket(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    component.ngOnInit();
    fixture.detectChanges();
    expect(debugElement.querySelector('.visible-xs.visible-sm.hidden > a').textContent).toBe('Back');
  
  });

  it('should create a copy of workbasket when workbasket is selected', () => {
    expect(component.workbasketClone).toBeUndefined();
    component.ngOnInit();
    fixture.detectChanges();
    expect(component.workbasket.id).toEqual(component.workbasketClone.id);
  });
  
});

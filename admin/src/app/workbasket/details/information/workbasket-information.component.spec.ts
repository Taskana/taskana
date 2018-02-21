import { async, ComponentFixture, TestBed, fakeAsync } from '@angular/core/testing';
import { WorkbasketService } from '../../../services/workbasketservice.service';
import { WorkbasketInformationComponent } from './workbasket-information.component';
import { FormsModule } from '@angular/forms';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';
import { Workbasket } from 'app/model/workbasket';
import { ICONTYPES, IconTypeComponent } from '../../../shared/type-icon/icon-type.component';
import { MapValuesPipe } from '../../../pipes/map-values.pipe';
import { RemoveNoneTypePipe } from '../../../pipes/remove-none-type';



describe('InformationComponent', () => {
  let component: WorkbasketInformationComponent;
  let fixture: ComponentFixture<WorkbasketInformationComponent>;
  let debugElement;
  
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketInformationComponent, IconTypeComponent, MapValuesPipe, RemoveNoneTypePipe],
      imports:[FormsModule, AngularSvgIconModule, HttpClientModule, HttpModule],
      providers:[WorkbasketService]

    })
    .compileComponents();
    fixture = TestBed.createComponent(WorkbasketInformationComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
  }));

  afterEach(() =>{
    document.body.removeChild(debugElement);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  
  it('should create a panel with heading and form with all fields', async(() => {
    component.workbasket = new Workbasket('id','created','keyModified','domain','type','modified','name','description','owner','custom1','custom2','custom3','custom4','orgLevel1','orgLevel2','orgLevel3','orgLevel4',null);
    fixture.detectChanges();
    expect(debugElement.querySelector('#wb-information')).toBeDefined();
    expect(debugElement.querySelector('#wb-information > .panel-heading > h4').textContent).toBe('name');
    expect(debugElement.querySelectorAll('#wb-information > .panel-body > form').length).toBe(2);
    fixture.whenStable().then(() => {
      expect(debugElement.querySelector('#wb-information > .panel-body > form:first-child > div:first-child > input').value).toBe('keyModified');
    });
    
  }));

  it('selectType should set workbasket.type to personal with 0 and group in other case', () => {
    component.workbasket = new Workbasket(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null);
    expect(component.workbasket.type).toEqual(null);
    component.selectType(ICONTYPES.PERSONAL);
    expect(component.workbasket.type).toEqual('PERSONAL');
    component.selectType(ICONTYPES.GROUP);
    expect(component.workbasket.type).toEqual('GROUP');
  });

});

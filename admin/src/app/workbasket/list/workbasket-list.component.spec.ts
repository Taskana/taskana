import { Component } from '@angular/core';
import { async, ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';
import { WorkbasketListComponent } from './workbasket-list.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { WorkbasketSummary } from '../../model/workbasketSummary';
import { WorkbasketService, Direction } from '../../services/workbasketservice.service';
import { HttpModule } from '@angular/http';
import { Router, Routes } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { Observable } from 'rxjs/Observable';
import { MapValuesPipe } from '../../pipes/map-values.pipe';

@Component({
  selector: 'dummy-detail',
  template: 'dummydetail'
})
export class DummyDetailComponent {

}

const  workbasketSummary: WorkbasketSummary[] = [ new WorkbasketSummary("1", "key1", "NAME1", "description 1", "owner 1", "", "", "PERSONAL", "", "", "", ""),
                                                  new WorkbasketSummary("2", "key2", "NAME2", "description 2", "owner 2", "", "", "MULTIPLE", "", "", "", "")
                                                ];


describe('WorkbasketListComponent', () => {
  let component: WorkbasketListComponent;
  let fixture: ComponentFixture<WorkbasketListComponent>;
  let debugElement: any = undefined;
  let workbasketService: WorkbasketService;

  const routes: Routes = [
    { path: ':id', component: DummyDetailComponent, outlet: 'detail' }
  ];

  
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkbasketListComponent, DummyDetailComponent, MapValuesPipe],
      
      imports:[
        AngularSvgIconModule,
        HttpModule,
        HttpClientModule,
        RouterTestingModule.withRoutes(routes)
      ],
      providers:[WorkbasketService]
    })
    .compileComponents();
    

    fixture = TestBed.createComponent(WorkbasketListComponent);
    component = fixture.componentInstance;
    debugElement = fixture.debugElement.nativeElement;
    workbasketService = TestBed.get(WorkbasketService);
    spyOn(workbasketService, 'getWorkBasketsSummary').and.returnValue(Observable.of(workbasketSummary));   
    spyOn(workbasketService, 'getSelectedWorkBasket') .and.returnValue(Observable.of('2'));

    fixture.detectChanges();
  }));

  afterEach(() =>{
    document.body.removeChild(debugElement);
  })

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should call workbasketService.getWorkbasketsSummary method on init', () => {
    component.ngOnInit();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalled();
    workbasketService.getWorkBasketsSummary().subscribe(value => {
      expect(value).toBe(workbasketSummary);
    })
  });

  it('should have wb-action-toolbar, wb-search-bar, wb-list-container and wb-pagination created in the html',() => {
    expect(debugElement.querySelector('#wb-action-toolbar')).toBeDefined();
    expect(debugElement.querySelector('#wb-search-bar')).toBeDefined();
    expect(debugElement.querySelector('#wb-pagination')).toBeDefined();
    expect(debugElement.querySelector('#wb-list-container')).toBeDefined();
    expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(4);
  });

  it('should have two workbasketsummary rows created with the second one selected.',() => {
    expect(debugElement.querySelectorAll('#wb-list-container > li').length).toBe(4);
    expect(debugElement.querySelectorAll('#wb-list-container > li')[2].getAttribute('class')).toBe('list-group-item');
    expect(debugElement.querySelectorAll('#wb-list-container > li')[3].getAttribute('class')).toBe('list-group-item active');
  });

  it('should have two workbasketsummary rows created with two different icons: user and users',() => {
    expect(debugElement.querySelectorAll('#wb-list-container > li')[2].querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/user.svg');
    expect(debugElement.querySelectorAll('#wb-list-container > li')[3].querySelector('svg-icon').getAttribute('ng-reflect-src')).toBe('./assets/icons/users.svg');
  });

  it('should have rendered sort by: name, id, description, owner and type',  () => {
    expect(debugElement.querySelector('#sort-by-name')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-key')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-description')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-owner')).toBeDefined();
    expect(debugElement.querySelector('#sort-by-type')).toBeDefined();
    
  });

  it('should have changed sort direction and perform a request after clicking sort direction buttons', fakeAsync( () => {
    debugElement.querySelector('#sort-by-direction-desc').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('key', Direction.DESC);
    debugElement.querySelector('#sort-by-direction-asc').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('key', Direction.ASC);
  }));

  it('should have changed sortBy field and perform a request after clicking on to any sort by rows', fakeAsync( () => {
    debugElement.querySelector('#sort-by-key').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('key', jasmine.any(String));
    debugElement.querySelector('#sort-by-name').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('name', jasmine.any(String));
    debugElement.querySelector('#sort-by-description').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('description', jasmine.any(String));
    debugElement.querySelector('#sort-by-owner').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('owner', jasmine.any(String));
    debugElement.querySelector('#sort-by-type').click();
    expect(workbasketService.getWorkBasketsSummary).toHaveBeenCalledWith('type', jasmine.any(String));
  }));

});

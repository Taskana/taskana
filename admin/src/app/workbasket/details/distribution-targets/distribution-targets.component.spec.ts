import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule, JsonpModule } from '@angular/http';

import { DistributionTargetsComponent } from './distribution-targets.component';
import { SpinnerComponent } from '../../../shared/spinner/spinner.component';
import { GeneralMessageModalComponent } from '../../../shared/general-message-modal/general-message-modal.component';
import { IconTypeComponent } from '../../../shared/type-icon/icon-type.component';
import { SelectWorkBasketPipe } from '../../../pipes/seleted-workbasket.pipe';
import { WorkbasketSummaryResource } from '../../../model/workbasket-summary-resource';
import { WorkbasketSummary } from '../../../model/workbasket-summary';
import { Links } from '../../../model/links';
import { Component } from '@angular/core';
import { WorkbasketService } from '../../../services/workbasket.service';
import { AlertService } from '../../../services/alert.service';
import { Observable } from 'rxjs/Observable';
import { Workbasket } from '../../../model/workbasket';

const workbasketSummaryResource: WorkbasketSummaryResource = new WorkbasketSummaryResource({
  'workbaskets': new Array<WorkbasketSummary>(
    new WorkbasketSummary("1", "key1", "NAME1", "description 1", "owner 1", "", "", "PERSONAL", "", "", "", ""),
    new WorkbasketSummary("2", "key2", "NAME2", "description 2", "owner 2", "", "", "GROUP", "", "", "", ""))
}, new Links({ 'href': 'url' }));

@Component({
  selector: 'taskana-filter',
  template: ''
})
export class FilterComponent {

}


describe('DistributionTargetsComponent', () => {
  let component: DistributionTargetsComponent;
  let fixture: ComponentFixture<DistributionTargetsComponent>;
  let workbasketService;
  let workbasket = new Workbasket('1', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }, { 'href': 'someurl' }, { 'href': 'someurl' }));

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, HttpModule, JsonpModule],
      declarations: [DistributionTargetsComponent, SpinnerComponent, GeneralMessageModalComponent, FilterComponent, SelectWorkBasketPipe, IconTypeComponent],
      providers: [WorkbasketService, AlertService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DistributionTargetsComponent);
    component = fixture.componentInstance;
    component.workbasket = workbasket;
    workbasketService = TestBed.get(WorkbasketService);
    spyOn(workbasketService, 'getWorkBasketsSummary').and.callFake(() => {
      return Observable.of(new WorkbasketSummaryResource(
        { 'workbaskets': new Array<WorkbasketSummary>(new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }))) }, new Links({ 'href': 'someurl' })))
    })
    spyOn(workbasketService, 'getWorkBasketsDistributionTargets').and.callFake(() => {
      return Observable.of(new WorkbasketSummaryResource(
        { 'workbaskets': new Array<WorkbasketSummary>(new WorkbasketSummary('id1', '', '', '', '', '', '', '', '', '', '', '', new Links({ 'href': 'someurl' }))) }, new Links({ 'href': 'someurl' })))
    })

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

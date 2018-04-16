import { Input, Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

import { TaskanaTreeComponent } from './tree.component';

import { TreeService } from 'app/services/tree/tree.service';

// tslint:disable:component-selector
@Component({
  selector: 'tree-root',
  template: ''
})
class TreeVendorComponent {
  @Input() options;
  @Input() state;
  @Input() nodes;
  treeModel = {
    getActiveNode() { }
  }
}
// tslint:enable:component-selector

describe('TaskanaTreeComponent', () => {
  let component: TaskanaTreeComponent;
  let fixture: ComponentFixture<TaskanaTreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, HttpModule],
      declarations: [TaskanaTreeComponent, TreeVendorComponent],
      providers: [TreeService]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskanaTreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

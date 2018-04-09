import { Input, Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TaskanaTreeComponent } from './tree.component';

import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http';

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
      declarations: [TaskanaTreeComponent, TreeVendorComponent]
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

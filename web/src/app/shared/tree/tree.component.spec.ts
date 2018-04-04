import { Input, Component } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreeComponent } from './tree.component';

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
}

// tslint:enable:component-selector
fdescribe('TreeComponent', () => {
  let component: TreeComponent;
  let fixture: ComponentFixture<TreeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, HttpModule],
      declarations: [TreeComponent, TreeVendorComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TreeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

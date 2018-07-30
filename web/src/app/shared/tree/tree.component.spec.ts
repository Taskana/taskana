import { Input, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { TaskanaTreeComponent } from './tree.component';

import { TreeService } from 'app/services/tree/tree.service';
import {
  ClassificationCategoriesService
} from 'app/administration/services/classification-categories-service/classification-categories.service';
import { configureTests } from 'app/app.test.configuration';
import { Pair } from 'app/models/pair';

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
  let classificationCategoriesService;


  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule, HttpClientModule],
        declarations: [TreeVendorComponent],
        providers: [TreeService, ClassificationCategoriesService]

      })
    };
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(TaskanaTreeComponent);
      classificationCategoriesService = testBed.get(ClassificationCategoriesService);
      spyOn(classificationCategoriesService, 'getCategoryIcon').and.returnValue(new Pair('assets/icons/categories/external.svg'));
      component = fixture.componentInstance;
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

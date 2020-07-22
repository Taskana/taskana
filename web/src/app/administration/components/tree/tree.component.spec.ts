import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { HttpClientModule } from '@angular/common/http';

import { configureTests } from 'app/app.test.configuration';
import { NgxsModule, Store } from '@ngxs/store';
import { ClassificationSelectors } from 'app/shared/store/classification-store/classification.selectors';
import { of } from 'rxjs';
import { Location } from '@angular/common';
import { UpdateClassification } from 'app/shared/store/classification-store/classification.actions';
import { TaskanaTreeComponent } from './tree.component';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { Classification } from '../../../shared/models/classification';

@Component({
  selector: 'tree-root',
  template: ''
})
class TreeVendorComponent {
  @Input() options;
  @Input() state;
  @Input() nodes;
}

describe('TaskanaTreeComponent', () => {
  let component: TaskanaTreeComponent;
  let fixture: ComponentFixture<TaskanaTreeComponent>;
  let classificationsService;
  let moveNodeEvent;
  let dropEvent;

  const locationSpy: jasmine.SpyObj<Location> = jasmine.createSpyObj('Location', ['go', 'path']);
  const storeSpy: jasmine.SpyObj<Store> = jasmine.createSpyObj('Store', ['select', 'dispatch']);

  const configure = (testBed: TestBed) => {
    testBed.configureTestingModule({
      imports: [AngularSvgIconModule, HttpClientModule, NgxsModule.forRoot()],
      declarations: [TreeVendorComponent],
      providers: [
        ClassificationsService,
        { provide: Location, useValue: locationSpy },
        { provide: Store, useValue: storeSpy }
      ]
    });
  };

  beforeEach((done) => {
    configureTests(configure).then((testBed) => {
      locationSpy.path.and.callFake(() => '');
      storeSpy.select.and.callFake((selector) => {
        switch (selector) {
          case ClassificationSelectors.selectedClassificationId:
            return of('id4');
          case ClassificationSelectors.classifications:
            return of([{ classificationId: 'id4' }]);
          default:
            return of();
        }
      });
      storeSpy.dispatch.and.callFake(() => of());

      fixture = testBed.createComponent(TaskanaTreeComponent);
      classificationsService = testBed.get(ClassificationsService);
      moveNodeEvent = {
        eventName: 'moveNode',
        node: { classificationId: 'id4', parentId: '', parentKey: '', _links: { self: { href: 'url' } } },
        to: { parent: { classificationId: 'id3', key: 'key3' } }
      };

      dropEvent = {
        event: { target: { tagName: 'TREE-VIEWPORT' } },
        element: { data: { classificationId: 'id3', parentId: 'id1', parentKey: 'key1' } }
      };

      component = fixture.componentInstance;
      fixture.detectChanges();
      done();
    });
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should be change the classification parent (onMoveNode)', async () => {
    const classification: Classification = {
      classificationId: 'id4',
      key: 'key4',
      category: 'MANUAL',
      domain: 'DOMAIN_A',
      parentId: '',
      parentKey: '',
      type: 'TASK',
      isValidInDomain: true,
      created: '019-04-10T10:23:34.985Z',
      modified: '2019-04-10T10:23:34.985Z',
      name: 'classification4',
      description: 'description',
      priority: 1,
      serviceLevel: 'level'
    };

    // using parameter 'any' since getClassification is a private method
    spyOn<any>(component, 'getClassification').and.returnValue(classification);
    spyOn(component, 'switchTaskanaSpinner');

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');

    await component.onMoveNode(moveNodeEvent);

    expect(classification.parentId).toEqual('id3');
    expect(classification.parentKey).toEqual('key3');
    expect(storeSpy.dispatch).toHaveBeenCalledWith(new UpdateClassification(classification));
  });

  it('should be changed the parent classification to root node (onDrop)', async () => {
    const classification: Classification = {
      classificationId: 'id3',
      key: 'key3',
      parentId: 'id1',
      parentKey: 'key1',
      category: 'MANUAL',
      domain: 'DOMAIN_A',
      type: 'TASK',
      isValidInDomain: true,
      created: '019-04-10T10:23:34.985Z',
      modified: '2019-04-10T10:23:34.985Z',
      name: 'classification3',
      description: 'description',
      priority: 1,
      serviceLevel: 'level'
    };

    // using parameter 'any' since getClassification is a private method
    spyOn<any>(component, 'getClassification').and.returnValue(classification);
    spyOn(component, 'switchTaskanaSpinner');

    expect(classification.parentId).toEqual('id1');
    expect(classification.parentKey).toEqual('key1');

    await component.onDrop(dropEvent);

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');
  });
});

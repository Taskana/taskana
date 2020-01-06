import {Component, Input} from '@angular/core';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {HttpClientModule} from '@angular/common/http';


import {TreeService} from 'app/services/tree/tree.service';
import {configureTests} from 'app/app.test.configuration';
import {Pair} from 'app/models/pair';
import {TaskanaTreeComponent} from './tree.component';
import {ClassificationDefinition} from '../../models/classification-definition';
import {LinksClassification} from '../../models/links-classfication';
import {ClassificationCategoriesService} from '../services/classifications/classification-categories.service';
import {ClassificationsService} from '../services/classifications/classifications.service';

@Component({
  selector: 'tree-root',
  template: ''
})
class TreeVendorComponent {
  @Input() options;
  @Input() state;
  @Input() nodes;
  treeModel = {
    getActiveNode() {
    }
  }
}

describe('TaskanaTreeComponent', () => {
  let component: TaskanaTreeComponent;
  let fixture: ComponentFixture<TaskanaTreeComponent>;
  let classificationCategoriesService;
  let classificationsService;
  let moveNodeEvent;
  let dropEvent;

  beforeEach(done => {
    const configure = (testBed: TestBed) => {
      testBed.configureTestingModule({
        imports: [AngularSvgIconModule, HttpClientModule],
        declarations: [TreeVendorComponent],
        providers: [TreeService, ClassificationCategoriesService, ClassificationsService]

      })
    };
    configureTests(configure).then(testBed => {
      fixture = testBed.createComponent(TaskanaTreeComponent);
      classificationCategoriesService = testBed.get(ClassificationCategoriesService);
      spyOn(classificationCategoriesService, 'getCategoryIcon').and.returnValue(new Pair('assets/icons/categories/external.svg'));
      classificationsService = TestBed.get(ClassificationsService);
      spyOn(classificationsService, 'putClassification').and.callFake((url, classification) => classification);
      moveNodeEvent = {
        eventName: 'moveNode',
        node: {
          classificationId: 'id4',
          parentId: '',
          parentKey: '',
          _links: {
            self: {
              href: 'url'
            }
          }
        },
        to: {
          parent: {
            classificationId: 'id3',
            key: 'key3'
          }
        }
      };

      dropEvent = {
        event: {
          target: {
            tagName: 'TREE-VIEWPORT'
          }
        },
        element: {
          data: {
            classificationId: 'id3',
            parentId: 'id1',
            parentKey: 'key1'
          }
        }
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
    spyOn(classificationsService, 'getClassification').and.returnValue(new ClassificationDefinition('id4',
      'key4', '', '', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification4', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({href: ''}, '', '', {href: ''}, {href: ''}, {href: ''})));
    spyOn(component, 'switchTaskanaSpinner');
    const classification = classificationsService.getClassification();
    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');

    await component.onMoveNode(moveNodeEvent);

    expect(classification.parentId).toEqual('id3');
    expect(classification.parentKey).toEqual('key3');
    expect(classificationsService.putClassification).toHaveBeenCalledWith(classification._links.self.href, classification);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });

  it('should be changed the parent classification to root node (onDrop)', async () => {
    spyOn(classificationsService, 'getClassification').and.returnValue(new ClassificationDefinition('id3',
      'key3', 'id1', 'key1', 'MANUAL', 'DOMAIN_A', 'TASK', true, '019-04-10T10:23:34.985Z', '2019-04-10T10:23:34.985Z',
      'classification3', 'description', 1, 'level', '', '', '', '', '', '',
      '', '', '', new LinksClassification({href: ''}, '', '', {href: ''}, {href: ''}, {href: ''})));
    spyOn(component, 'switchTaskanaSpinner');
    const classification = classificationsService.getClassification();
    expect(classification.parentId).toEqual('id1');
    expect(classification.parentKey).toEqual('key1');

    await component.onDrop(dropEvent);

    expect(classification.parentId).toEqual('');
    expect(classification.parentKey).toEqual('');
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(true);
    expect(component.switchTaskanaSpinner).toHaveBeenCalledWith(false);
  });
});

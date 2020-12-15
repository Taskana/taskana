import {
  AfterViewChecked,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import { TreeNodeModel } from 'app/administration/models/tree-node';

import { ITreeOptions, KEYS, TREE_ACTIONS, TreeComponent } from 'angular-tree-component';
import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, takeUntil } from 'rxjs/operators';
import { Select, Store } from '@ngxs/store';
import { EngineConfigurationSelectors } from 'app/shared/store/engine-configuration-store/engine-configuration.selectors';

import { Location } from '@angular/common';
import { NOTIFICATION_TYPES } from 'app/shared/models/notifications';
import { NotificationService } from 'app/shared/services/notifications/notification.service';
import { Classification } from '../../../shared/models/classification';
import { ClassificationsService } from '../../../shared/services/classifications/classifications.service';
import { ClassificationCategoryImages } from '../../../shared/models/customisation';
import { ClassificationSelectors } from '../../../shared/store/classification-store/classification.selectors';
import {
  DeselectClassification,
  SelectClassification,
  UpdateClassification
} from '../../../shared/store/classification-store/classification.actions';
import { ClassificationTreeService } from '../../services/classification-tree.service';
import { Pair } from '../../../shared/models/pair';

@Component({
  selector: 'taskana-administration-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class TaskanaTreeComponent implements OnInit, AfterViewChecked, OnDestroy {
  treeNodes: TreeNodeModel[];

  @Input() selectNodeId: string;
  @Input() filterText: string;
  @Input() filterIcon = '';
  @Output() switchTaskanaSpinnerEmit = new EventEmitter<boolean>();
  @Select(EngineConfigurationSelectors.selectCategoryIcons) categoryIcons$: Observable<ClassificationCategoryImages>;
  @Select(ClassificationSelectors.selectedClassificationId) selectedClassificationId$: Observable<string>;
  @Select(ClassificationSelectors.classifications) classifications$: Observable<Classification[]>;
  @Select(ClassificationSelectors.selectedClassificationType) classificationTypeSelected$: Observable<string>;

  options: ITreeOptions = {
    displayField: 'name',
    idField: 'classificationId',
    actionMapping: {
      keys: {
        [KEYS.ENTER]: TREE_ACTIONS.TOGGLE_ACTIVE,
        [KEYS.SPACE]: TREE_ACTIONS.TOGGLE_EXPANDED
      }
    },
    useVirtualScroll: true,
    levelPadding: 20,
    allowDrag: true,
    allowDrop: true
  };

  @ViewChild('tree', { static: true })
  private tree: TreeComponent;

  private filterTextOld: string;
  private filterIconOld = '';
  private destroy$ = new Subject<void>();

  constructor(
    private elementRef: ElementRef,
    private classificationsService: ClassificationsService,
    private location: Location,
    private store: Store,
    private notificationsService: NotificationService,
    private classificationTreeService: ClassificationTreeService
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event) {
    if (this.checkValidElements(event) && this.tree.treeModel.getActiveNode()) {
      this.deselectActiveNode();
    }
  }

  ngOnInit() {
    const computedTreeNodes$: Observable<TreeNodeModel[]> = this.classifications$.pipe(
      filter((classifications) => typeof classifications !== 'undefined'),
      map((classifications) => this.classificationTreeService.transformToTreeNode(classifications))
    );

    combineLatest([this.selectedClassificationId$, computedTreeNodes$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([selectedClassificationId, treeNodes]) => {
        this.treeNodes = treeNodes;
        this.selectNodeId = typeof selectedClassificationId !== 'undefined' ? selectedClassificationId : undefined;
        if (typeof this.tree.treeModel.getActiveNode() !== 'undefined') {
          if (this.tree.treeModel.getActiveNode().data.classificationId !== this.selectNodeId) {
            // wait for angular's two-way binding to convert the treeNodes to the internal tree structure.
            // after that conversion the new treeNodes are available
            setTimeout(() => this.selectNode(this.selectNodeId), 0);
          }
        }
      });

    this.classificationTypeSelected$.pipe(takeUntil(this.destroy$)).subscribe(() => {
      if (this.tree.treeModel.getActiveNode()) {
        this.deselectActiveNode();
      }
    });
  }

  ngAfterViewChecked(): void {
    if (this.selectNodeId && !this.tree.treeModel.getActiveNode()) {
      this.selectNode(this.selectNodeId);
    }

    if (typeof this.selectNodeId !== 'undefined') {
      if (typeof this.getNode(this.selectNodeId) !== 'undefined') {
        this.getNode(this.selectNodeId).ensureVisible();
      }
    }

    if (this.filterTextOld !== this.filterText || this.filterIconOld !== this.filterIcon) {
      this.filterIconOld = this.filterIcon;
      this.filterTextOld = this.filterText;
      this.filterNodes(this.filterText ? this.filterText : '', this.filterIcon);
      this.manageTreeState();
    }
  }

  onActivate(treeNode: any) {
    const id = treeNode.node.data.classificationId;
    this.selectNodeId = id;
    this.store.dispatch(new SelectClassification(id));
    this.location.go(this.location.path().replace(/(classifications).*/g, `classifications/(detail:${id})`));
  }

  onDeactivate(event: any) {
    if (!event.treeModel.activeNodes.length) {
      this.store.dispatch(new DeselectClassification());
      this.location.go(this.location.path().replace(/(classifications).*/g, 'classifications'));
    }
  }

  async onMoveNode($event) {
    this.switchTaskanaSpinner(true);
    const classification = await this.getClassification($event.node.classificationId);
    classification.parentId = $event.to.parent.classificationId;
    classification.parentKey = $event.to.parent.key;
    this.collapseParentNodeIfItIsTheLastChild($event.node);
    this.updateClassification(classification);
  }

  async onDrop($event) {
    if ($event.event.target.tagName === 'TREE-VIEWPORT') {
      this.switchTaskanaSpinner(true);
      const classification = await this.getClassification($event.element.data.classificationId);
      this.collapseParentNodeIfItIsTheLastChild($event.element.data);
      classification.parentId = '';
      classification.parentKey = '';
      this.updateClassification(classification);
    }
  }

  getCategoryIcon(category: string): Observable<Pair<string, string>> {
    return this.categoryIcons$.pipe(
      map((iconMap) =>
        iconMap[category]
          ? { left: iconMap[category], right: category }
          : { left: iconMap.missing, right: 'Category does not match with the configuration' }
      )
    );
  }

  switchTaskanaSpinner(active: boolean) {
    this.switchTaskanaSpinnerEmit.emit(active);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private selectNode(nodeId: string) {
    if (nodeId) {
      const selectedNode = this.getNode(nodeId);
      if (selectedNode) {
        selectedNode.setIsActive(true);
      }
    }
  }

  private deselectActiveNode() {
    const activeNode = this.tree.treeModel.getActiveNode();
    delete this.selectNodeId;
    activeNode.setIsActive(false);
    activeNode.blur();
  }

  private getNode(nodeId: string) {
    return this.tree.treeModel.getNodeById(nodeId);
  }

  private filterNodes(text, iconText) {
    this.tree.treeModel.filterNodes(
      (node) => TaskanaTreeComponent.checkNameAndKey(node, text) && TaskanaTreeComponent.checkIcon(node, iconText)
    );
  }

  private static checkNameAndKey(node: any, text: string): boolean {
    return (
      node.data.name.toUpperCase().includes(text.toUpperCase()) ||
      node.data.key.toUpperCase().includes(text.toUpperCase())
    );
  }

  private static checkIcon(node: any, iconText: string): boolean {
    return node.data.category.toUpperCase() === iconText.toUpperCase() || iconText === '';
  }

  private manageTreeState() {
    this.tree.treeModel.collapseAll();
    if (this.filterText === '') {
      this.tree.treeModel.collapseAll();
    }
  }

  private checkValidElements(event): boolean {
    return (
      (this.elementRef.nativeElement.contains(event.target) || this.elementRef.nativeElement === event.target) &&
      (event.target.localName === 'tree-viewport' || event.target.localName === 'taskana-tree')
    );
  }

  private getClassification(classificationId: string): Promise<Classification> {
    return this.classificationsService.getClassification(classificationId).toPromise();
  }

  private updateClassification(classification: Classification) {
    this.store.dispatch(new UpdateClassification(classification)).subscribe(() => {
      this.notificationsService.showToast(
        NOTIFICATION_TYPES.SUCCESS_ALERT_5,
        new Map<string, string>([['classificationKey', classification.key]])
      );
      this.switchTaskanaSpinner(false);
    });
  }

  private collapseParentNodeIfItIsTheLastChild(node: any) {
    if (node.parentId.length > 0 && this.getNode(node.parentId) && this.getNode(node.parentId).children.length < 2) {
      this.tree.treeModel.update();
      this.getNode(node.parentId).collapse();
    }
  }
}

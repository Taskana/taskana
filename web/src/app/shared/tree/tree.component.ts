import { Component, OnInit, Input, Output, EventEmitter, ViewChild, AfterViewChecked, OnChanges, SimpleChanges } from '@angular/core';
import { TreeNodeModel } from 'app/models/tree-node';

import { TREE_ACTIONS, KEYS, IActionMapping, ITreeOptions, ITreeState, TreeComponent, TreeNode } from 'angular-tree-component';
import { TreeService } from '../../services/tree/tree.service';

@Component({
  selector: 'taskana-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class TaskanaTreeComponent implements OnInit, AfterViewChecked {


  @ViewChild('tree')
  private tree: TreeComponent;

  @Input() treeNodes: TreeNodeModel;
  @Output() treeNodesChange = new EventEmitter<Array<TreeNodeModel>>();
  @Input() selectNodeId: string;
  @Output() selectNodeIdChanged = new EventEmitter<string>();
  @Input() filterText: string;

  private filterTextOld: string

  options: ITreeOptions = {
    displayField: 'name',
    idField: 'classificationId',
    actionMapping: {
      keys: {
        [KEYS.ENTER]: (tree, node, $event) => {
          node.toggleExpanded();
        }
      }
    },
    animateExpand: true,
    animateSpeed: 20,
    levelPadding: 20
  }

  constructor(private treeService: TreeService) { }

  ngOnInit() {
    this.selectNode(this.selectNodeId);
    this.treeService.getRemovedNodeId().subscribe(value => {
      const removedNode = this.getNode(value);
      if (removedNode.parent) {
        removedNode.parent.collapse();
      }
    });
  }


  ngAfterViewChecked(): void {
    if (this.selectNodeId && !this.tree.treeModel.getActiveNode()) {
      this.selectNode(this.selectNodeId);
    } else if (!this.selectNodeId && this.tree.treeModel.getActiveNode()) {
      this.unSelectActiveNode();
    }
    if (this.filterTextOld !== this.filterText) {
      this.filterTextOld = this.filterText;
      this.filterNodes(this.filterText)
    }
  }

  onActivate(treeNode: any) {
    this.selectNodeIdChanged.emit(treeNode.node.data.classificationId + '');
  }

  onDeactivate(treeNode: any) {
    this.selectNodeIdChanged.emit(undefined);
  }

  private selectNode(nodeId: string) {
    if (nodeId) {
      const selectedNode = this.getNode(nodeId)
      if (selectedNode) {
        selectedNode.setIsActive(true)
        this.expandParent(selectedNode);
      }
    }
  }

  private unSelectActiveNode() {
    const activeNode = this.tree.treeModel.getActiveNode();
    activeNode.setIsActive(false);
    activeNode.blur();
  }

  private getNode(nodeId: string) {
    return this.tree.treeModel.getNodeById(nodeId);
  }

  private expandParent(node: TreeNode) {
    if (!node.parent) {
      return
    }
    node.parent.expand();
    this.expandParent(node.parent);
  }

  private filterNodes(text) {
    this.tree.treeModel.filterNodes((node) => {
      return (node.data.name.toUpperCase().includes(text.toUpperCase())
           || node.data.key.toUpperCase().includes(text.toUpperCase()));
    });
  }

}



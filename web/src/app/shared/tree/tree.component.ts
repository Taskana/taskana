import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { TreeNode } from 'app/models/tree-node';
import { TREE_ACTIONS, KEYS, IActionMapping, ITreeOptions, ITreeState } from 'angular-tree-component';

@Component({
  selector: 'taskana-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.scss']
})
export class TreeComponent implements OnInit {

  @Input() treeNodes: TreeNode;
  @Output() treeNodesChange = new EventEmitter<Array<TreeNode>>();

  options: ITreeOptions = {
    displayField: 'name',
    idField: 'id',
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

  state: ITreeState = {
    activeNodeIds: { ['']: true },
  }

  constructor() { }

  ngOnInit() {

  }

}



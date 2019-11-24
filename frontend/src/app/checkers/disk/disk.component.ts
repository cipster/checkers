import {Component, Input} from '@angular/core';
import {Disk} from "../domain/disk";

@Component({
  selector: 'app-disk',
  templateUrl: './disk.component.html',
  styleUrls: ['./disk.component.scss']
})
export class DiskComponent {
  @Input() disk: Disk;

  constructor() {
  }
}

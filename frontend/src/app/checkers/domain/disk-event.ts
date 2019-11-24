import {Disk} from "./disk";
import {Move} from "./move";

export declare type DISK_EVENT_TYPE = 'SELECT' | 'MOVE';

export interface DiskEvent {
  disk: Disk;
  type: DISK_EVENT_TYPE;
  move?: Move;
}

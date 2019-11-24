import {DISK_COLOR} from "./disk";
import {Move} from "./move";

export interface Turn {
  color: DISK_COLOR;
  move?: Move;
  possibleMoves?: Move[];
}

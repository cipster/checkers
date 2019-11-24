import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Game} from "../domain/game";
import {Move} from "../domain/move";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  constructor(private readonly http: HttpClient) {
  }

  startGame() {
    return this.http.post('/api/v1/game', {});
  }

  moveDisk(game: Game, move: Move): Observable<Game> {
    return this.http.post<Game>(`/api/v1/game/${game.id}/moves`, move);
  }
}

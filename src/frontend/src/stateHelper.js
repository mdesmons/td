/* Get the sorted ranks for a ladder */
export function getLadderRanks(state, ladderId) {
	return state.get('subscriptions')
    	.filter(s => s.get('ladderId') == ladderId)
    	.map(s => s.set('player', state.getIn(['players', s.get('playerId')])))
    	.sort((a,b ) => {
		  if (a.get('rank') < b.get('rank')) { return -1; }
		  if (a.get('rank') > b.get('rank')) { return 1; }
		  if (a.get('rank') === b.get('rank')) { return 0; }
		})
}

export function getFirstLast(player) {
	return player.get('firstname') + ' ' + player.get('lastname').toUpperCase()
}

// get the list of ladders this player subscribed to
export function getSubscribedLaddersForPlayer(state, playerId) {

	// first get the list of subscriptions for the player
	return state.get('subscriptions')
    	.filter(s => s.get('playerId') == playerId)
    	.valueSeq()
    	// then get the ladder ids for those subscriptions
    	.map(s => s.get('ladderId'))
    	// make them unique
    	.toSet()
    	// and get the corresponding ladder
    	.map(l => state.getIn(['ladders', l]))
}


// check if a player is subscribed to a ladder
export function isSubscribedToLadder(state, ladderId, playerId) {
	return (state.get('subscriptions').filter(s => s.get('playerId') == playerId && s.get('ladderId') == ladderId).size > 0)
}

/* Get a player's rank in a ladder */
export function getPlayerRankInLadder(state, ladderId, playerId) {
	return state.get('subscriptions').find(s => ((s.get('playerId') == playerId) && (s.get('ladderId') == ladderId)))
}

let currentId = null;

let match = null;

let allMatches = [];

let latestCommentary = [];

let toastTimer = null;


/* --------------------------------
   DOM HELPERS
-------------------------------- */

const $ = (selector) =>
  document.querySelector(selector);

const $$ = (selector) =>
  [...document.querySelectorAll(selector)];


/* --------------------------------
   API
-------------------------------- */

async function api(path, options) {

  const response = await fetch(path, options);

  if (!response.ok) {
    throw new Error(
      await response.text()
    );
  }

  return response.json();
}


/* --------------------------------
   SECURITY
-------------------------------- */

function escapeHtml(value) {

  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}


/* --------------------------------
   TEAM INITIALS
-------------------------------- */

function initials(team) {

  return String(team || "")
    .split(/\s+/)
    .map(part => part[0] || "")
    .join("")
    .slice(0, 3)
    .toUpperCase();
}


/* --------------------------------
   OVERS
-------------------------------- */

function oversFromBalls(balls) {

  const legalBalls =
    Number(balls || 0);

  return `
    ${Math.floor(legalBalls / 6)}.
    ${legalBalls % 6}
  `.trim();
}


/* --------------------------------
   DATE / TIME
-------------------------------- */

function formatTime(value) {

  if (!value) {
    return "—";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return "—";
  }

  return date.toLocaleTimeString(
    [],
    {
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit"
    }
  );
}


function relativeTime(value) {

  if (!value) {
    return "—";
  }

  const date =
    new Date(value);

  const seconds =
    Math.max(
      0,
      Math.round(
        (Date.now() -
          date.getTime()) /
        1000
      )
    );

  if (seconds < 5) {
    return "just now";
  }

  if (seconds < 60) {
    return `${seconds}s ago`;
  }

  const mins =
    Math.floor(seconds / 60);

  return `${mins}m ago`;
}


/* --------------------------------
   TOAST
-------------------------------- */

function showToast(message) {

  $("#toastText").textContent =
    message;

  $("#toast").classList.add("show");

  clearTimeout(toastTimer);

  toastTimer =
    setTimeout(
      () =>
        $("#toast")
          .classList
          .remove("show"),
      2200
    );
}


/* --------------------------------
   MATCH LIST
-------------------------------- */

function renderMatchList(
  items = allMatches
) {

  const list =
    $("#matchList");

  list.innerHTML = "";

  $("#liveCount").textContent =
    `${items.filter(
      matchItem =>
        matchItem.status === "LIVE"
    ).length} LIVE`;


  if (!items.length) {

    list.innerHTML =
      '<div class="empty">No matching fixtures.</div>';

    return;
  }


  items.forEach(m => {

    const el =
      document.createElement("button");

    el.type = "button";

    el.className =
      `match-item ${
        m.id === currentId
          ? "active"
          : ""
      }`;


    el.innerHTML = `

      <div class="match-top">

        <span class="match-format">
          ${escapeHtml(m.format)}
        </span>

        <span class="match-live">
          ${
            m.status === "LIVE"
              ? "● LIVE"
              : escapeHtml(m.status)
          }
        </span>

      </div>


      <div class="match-teams">

        <div class="match-team-row">

          <span class="match-team-name">
            ${escapeHtml(m.teamA)}
          </span>

          <span class="match-format">
            ${initials(m.teamA)}
          </span>

        </div>


        <div class="match-team-row">

          <span class="match-team-name">
            ${escapeHtml(m.teamB)}
          </span>

          <span class="match-format">
            ${initials(m.teamB)}
          </span>

        </div>

      </div>


      <div class="
        match-status
        ${m.status === "LIVE" ? "live" : ""}
      ">
        ${escapeHtml(m.venue)}
      </div>
    `;


    el.addEventListener(
      "click",
      () => loadMatch(m.id)
    );

    list.appendChild(el);

  });
}


/* --------------------------------
   LOAD ALL MATCHES
-------------------------------- */

async function loadMatches() {

  allMatches =
    await api(
      "/api/matches"
    );

  renderMatchList();

  if (
    !currentId &&
    allMatches.length
  ) {
    await loadMatch(
      allMatches[0].id
    );
  }
}


/* --------------------------------
   LOAD MATCH
-------------------------------- */

async function loadMatch(id) {

  currentId = id;

  match =
    await api(
      `/api/matches/${id}`
    );


  latestCommentary =
    await api(
      `/api/matches/${id}/commentary`
    );


  const innings =
    currentInnings();


  const stats =
    innings
      ? await api(
          `/api/matches/${id}/innings/${innings.id}/stats`
        )
      : [];


  renderMatch();

  renderBatting(stats);

  renderCommentary(
    latestCommentary
  );

  renderMomentum(
    latestCommentary
  );

  renderBowlingSummary(
    stats
  );

  renderMatchList();
}


/* --------------------------------
   CURRENT INNINGS
-------------------------------- */

function currentInnings() {

  if (
    !match?.innings?.length
  ) {
    return null;
  }

  return (
    match.innings.find(
      innings =>
        innings.numberInMatch ===
        match.currentInnings
    ) ||
    match.innings[0]
  );
}


/* --------------------------------
   RENDER MATCH
-------------------------------- */

function renderMatch() {

  const innings =
    currentInnings();

  if (!match || !innings) {
    return;
  }


  const battingTeam =
    innings.battingTeam ||
    match.teamA;

  const opponent =
    battingTeam === match.teamA
      ? match.teamB
      : match.teamA;


  const crr =
    innings.legalBalls
      ? (
          innings.runs /
          (innings.legalBalls / 6)
        ).toFixed(2)
      : "0.00";


  const target =
    innings.target ||
    (
      innings.numberInMatch === 2 &&
      match.innings[0]?.runs
        ? match.innings[0].runs + 1
        : null
    );


  const remaining =
    target
      ? Math.max(
          0,
          target - innings.runs
        )
      : null;


  /* HEADER */

  $("#venue").textContent =
    match.venue ||
    "Live Match";

  $("#formatBadge").textContent =
    match.format ||
    "MATCH";

  $("#startMeta").textContent =
    match.startTime
      ? `STARTED ${formatTime(
          match.startTime
        )}`
      : "LIVE MATCH";


  /* TEAMS */

  $("#teamA").textContent =
    match.teamA;

  $("#teamB").textContent =
    match.teamB;


  $("#teamAInitials").textContent =
    initials(match.teamA);

  $("#teamBInitials").textContent =
    initials(match.teamB);


  $("#teamACode").textContent =
    initials(match.teamA);

  $("#teamBCode").textContent =
    initials(match.teamB);


  /* SCORE */

  $("#score").textContent =
    `${innings.runs}/${innings.wickets}`;


  $("#overs").textContent =
    `${oversFromBalls(
      innings.legalBalls
    )} overs`;


  $("#runRate").textContent =
    `CRR ${crr}`;


  $("#opponentScore").textContent =
    innings.numberInMatch === 2
      ? `${match.innings?.[0]?.runs ?? 0}`
      : "—";


  $("#targetText").textContent =
    target
      ? `Target ${target}`
      : `${opponent} to bat`;


  $("#chaseStatus").textContent =
    remaining !== null
      ? `${remaining} to win`
      : `${opponent} next`;


  /* STATUS */

  $("#state").textContent =
    match.status;


  $("#updated").textContent =
    relativeTime(match.updatedAt);


  $("#inningsState").textContent =
    `${ordinal(
      innings.numberInMatch
    )} INNINGS`;


  $("#inningsLabel").textContent =
    `${battingTeam} • ${ordinal(
      innings.numberInMatch
    )} innings`;


  /* HERO STATS */

  $("#heroCrr").textContent =
    crr;

  $("#heroWickets").textContent =
    innings.wickets;

  $("#heroBalls").textContent =
    innings.legalBalls;

  $("#heroToss").textContent =
    match.tossWinner
      ? `${match.tossWinner} • ${match.tossDecision}`
      : "—";


  /* SUMMARY */

  $("#summary").innerHTML = `

    <div class="metric">
      <b>
        ${innings.runs}/${innings.wickets}
      </b>

      <span>
        Current score
      </span>
    </div>


    <div class="metric">
      <b>
        ${oversFromBalls(
          innings.legalBalls
        )}
      </b>

      <span>
        Legal overs
      </span>
    </div>


    <div class="metric">
      <b>
        ${crr}
      </b>

      <span>
        Current run rate
      </span>
    </div>


    <div class="metric">
      <b>
        ${escapeHtml(
          match.tossWinner || "—"
        )}
      </b>

      <span>
        Toss winner
      </span>
    </div>

  `;


  /* INNINGS HISTORY */

  const inningsHistory =
    $("#inningsHistory");


  inningsHistory.innerHTML =
    match.innings?.length

      ? match.innings
          .map(i => `

            <div class="innings-row">

              <div>

                <div class="innings-team">
                  ${escapeHtml(
                    i.battingTeam
                  )}
                </div>

                <div class="innings-number">
                  ${ordinal(
                    i.numberInMatch
                  )}
                  INNINGS
                </div>

              </div>


              <div class="innings-score">
                ${i.runs}/${i.wickets}
              </div>


              <div class="innings-target">
                ${
                  i.target
                    ? `Target ${i.target}`
                    : `${oversFromBalls(
                        i.legalBalls
                      )} ov`
                }
              </div>

            </div>

          `)
          .join("")

      : `
          <div class="empty">
            No innings recorded.
          </div>
        `;
}


/* --------------------------------
   ORDINAL
-------------------------------- */

function ordinal(number) {

  const value =
    Number(number || 1);

  if (value === 1) {
    return "1ST";
  }

  if (value === 2) {
    return "2ND";
  }

  if (value === 3) {
    return "3RD";
  }

  return `${value}TH`;
}


/* --------------------------------
   BATTING
-------------------------------- */

function renderBatting(rows) {

  const body =
    $("#batting");


  const battingRows =
    rows
      .filter(
        row =>
          row.runs ||
          row.balls
      )
      .sort(
        (a, b) =>
          (b.runs || 0) -
          (a.runs || 0)
      );


  body.innerHTML =
    battingRows.length

      ? battingRows
          .map(row => {

            const strikeRate =
              row.balls
                ? (
                    (row.runs /
                      row.balls) *
                    100
                  ).toFixed(1)
                : "0.0";


            const initialsValue =
              initials(
                row.player
              ).slice(0, 2);


            return `

              <tr>

                <td>

                  <div class="player-cell">

                    <span class="player-mark">
                      ${initialsValue}
                    </span>

                    <span class="player-copy">

                      <b>

                        ${escapeHtml(
                          row.player
                        )}

                        ${
                          row.out
                            ? `<span class="out-tag">
                                 OUT
                               </span>`
                            : ""
                        }

                      </b>

                      <small>
                        ${escapeHtml(
                          row.team || ""
                        )}
                      </small>

                    </span>

                  </div>

                </td>


                <td>
                  <strong>
                    ${row.runs}
                  </strong>
                </td>


                <td>
                  ${row.balls}
                </td>


                <td>
                  ${row.fours}
                </td>


                <td>
                  ${row.sixes}
                </td>


                <td>
                  ${strikeRate}
                </td>

              </tr>

            `;

          })
          .join("")

      : `

          <tr>

            <td colspan="6">

              <div class="empty">
                No batting events yet.
              </div>

            </td>

          </tr>

        `;
}


/* --------------------------------
   COMMENTARY
-------------------------------- */

function renderCommentary(rows) {

  const box =
    $("#commentary");


  const ordered =
    [...rows].reverse();


  box.innerHTML =
    ordered.length

      ? ordered
          .map(event => {

            const total =
              Number(
                event.runsOffBat || 0
              ) +
              Number(
                event.extras || 0
              );


            const flag =
              event.wicket
                ? `
                    <span class="event-flag">
                      • WICKET
                    </span>
                  `
                : "";


            return `

              <div class="comm">

                <div class="ball-chip">
                  ${event.overNumber}.
                  ${event.ballNumber}
                </div>


                <div class="comm-copy">

                  <p>
                    ${escapeHtml(
                      event.commentary ||
                      "Delivery recorded."
                    )}
                  </p>


                  <small>

                    ${escapeHtml(
                      event.bowler ||
                      "Bowler"
                    )}

                    to

                    ${escapeHtml(
                      event.striker ||
                      "Striker"
                    )}

                    •
                    ${total}

                    run${
                      total === 1
                        ? ""
                        : "s"
                    }

                    ${flag}

                  </small>

                </div>

              </div>

            `;

          })
          .join("")

      : `

          <div class="empty">
            Waiting for live deliveries…
          </div>

        `;
}


/* --------------------------------
   LAST 12 BALLS
-------------------------------- */

function renderMomentum(rows) {

  const box =
    $("#momentum");


  const latest =
    [...rows]
      .slice(-12)
      .reverse();


  $("#deliverySummary")
    .textContent =
      latest.length
        ? `${latest.length} recent balls`
        : "Waiting for feed";


  box.innerHTML =
    latest.length

      ? latest
          .map(event => {

            const total =
              Number(
                event.runsOffBat || 0
              ) +
              Number(
                event.extras || 0
              );


            const label =
              event.wicket
                ? "W"
                : total === 0
                  ? "·"
                  : total;


            const className =
              event.wicket
                ? "wicket"
                : `runs-${total}`;


            return `

              <div
                class="delivery-ball
                       ${className}"
                title="${escapeHtml(
                  event.commentary || ""
                )}"
              >

                <span>
                  ${event.overNumber}.
                  ${event.ballNumber}
                </span>

                <b>
                  ${label}
                </b>

              </div>

            `;

          })
          .join("")

      : `

          <div class="empty">
            No deliveries yet.
          </div>

        `;
}


/* --------------------------------
   BOWLING SUMMARY
-------------------------------- */

function renderBowlingSummary(rows) {

  const bowlers =
    rows
      .filter(
        row =>
          row.ballsBowled > 0 ||
          row.runsConceded > 0
      )
      .sort(
        (a, b) =>
          (b.wickets || 0) -
          (a.wickets || 0)
      );


  if (!bowlers.length) {
    return;
  }


  const best =
    bowlers[0];


  const extra =
    document.querySelector(
      "#summary"
    );


  if (
    !extra ||
    extra.querySelector(
      ".bowler-metric"
    )
  ) {
    return;
  }


  const node =
    document.createElement(
      "div"
    );


  node.className =
    "metric bowler-metric";


  node.innerHTML = `

    <b>
      ${escapeHtml(
        best.player
      )}
    </b>

    <span>
      Leading bowler ·
      ${best.wickets}
      wicket${
        best.wickets === 1
          ? ""
          : "s"
      }
    </span>

  `;


  extra.appendChild(node);
}


/* --------------------------------
   SCORE EVENT
-------------------------------- */

async function submitEvent({
  runs = 0,
  wicket = false
}) {

  if (
    !currentId ||
    !match
  ) {
    return;
  }


  const innings =
    currentInnings();


  const battingTeam =
    innings?.battingTeam ||
    match.teamA;


  const otherTeam =
    battingTeam === match.teamA
      ? match.teamB
      : match.teamA;


  try {

    const response =
      await api(
        `/api/matches/${currentId}/events`,
        {

          method: "POST",

          headers: {
            "Content-Type":
              "application/json"
          },

          body:
            JSON.stringify({

              runs,

              wicket,

              wicketType:
                wicket
                  ? "caught"
                  : "",

              commentary:
                wicket

                  ? "WICKET! Quick entry recorded a breakthrough."

                  : `${
                      runs === 0
                        ? "Dot ball"
                        : runs === 4
                          ? "FOUR!"
                          : runs === 6
                            ? "SIX!"
                            : `${runs} run${
                                runs === 1
                                  ? ""
                                  : "s"
                              }`
                    }. Live score updated.`,

              striker:
                "Virat Kohli",

              nonStriker:
                "Suryakumar Yadav",

              bowler:
                otherTeam === "Australia"
                  ? "Mitchell Starc"
                  : "Jasprit Bumrah",

              legalDelivery:
                true,

              extras:
                0

            })

        }
      );


    showToast(
      `Score updated · ${response.overs} overs`
    );


    await loadMatch(
      currentId
    );

  } catch (error) {

    showToast(
      "Score update failed"
    );

    console.error(error);
  }
}


/* --------------------------------
   REFRESH
-------------------------------- */

async function refresh() {

  if (currentId) {

    await loadMatch(
      currentId
    );

  } else {

    await loadMatches();

  }
}


/* --------------------------------
   SERVER SENT EVENTS
-------------------------------- */

function connect() {

  const source =
    new EventSource(
      "/api/live"
    );


  source.onopen = () => {

    $("#connection")
      .textContent =
      "LIVE CONNECTED";

    $("#statusDot")
      .className =
      "status-dot live";
  };


  source.onerror = () => {

    $("#connection")
      .textContent =
      "RECONNECTING";

    $("#statusDot")
      .className =
      "status-dot offline";
  };


  source.addEventListener(
    "connected",
    () => {

      $("#connection")
        .textContent =
        "LIVE CONNECTED";

      $("#statusDot")
        .className =
        "status-dot live";

    }
  );


  source.addEventListener(
    "score-update",
    async () => {

      if (!currentId) {
        return;
      }

      try {

        await loadMatch(
          currentId
        );

      } catch (error) {

        console.error(error);

      }

    }
  );
}


/* --------------------------------
   EVENT LISTENERS
-------------------------------- */

$("#refresh")
  .addEventListener(
    "click",
    refresh
  );


$("#simulate")
  .addEventListener(
    "click",
    () =>
      submitEvent({

        runs:
          [0, 1, 2, 4, 6][
            Math.floor(
              Math.random() * 5
            )
          ]

      })
  );


$("#matchSearch")
  .addEventListener(
    "input",
    event => {

      const query =
        event.target.value
          .trim()
          .toLowerCase();


      renderMatchList(

        allMatches.filter(
          m =>
            `
              ${m.teamA}
              ${m.teamB}
              ${m.venue}
              ${m.format}
            `
              .toLowerCase()
              .includes(query)
        )

      );

    }
  );


$$(
  ".run-buttons button"
).forEach(button => {

  button.addEventListener(
    "click",
    () =>
      submitEvent({
        runs:
          Number(
            button.dataset.run
          )
      })
  );

});


$(".wicket-button")
  .addEventListener(
    "click",
    () =>
      submitEvent({
        wicket: true,
        runs: 0
      })
  );


/* --------------------------------
   LIVE CLOCK
-------------------------------- */

setInterval(
  () => {

    $("#footerClock")
      .textContent =
      new Date().toLocaleTimeString(
        [],
        {
          hour: "2-digit",
          minute: "2-digit",
          second: "2-digit"
        }
      );


    if (match?.updatedAt) {

      $("#updated")
        .textContent =
        relativeTime(
          match.updatedAt
        );

    }

  },
  1000
);


/* --------------------------------
   INITIAL LOAD
-------------------------------- */

loadMatches()
  .then(connect)
  .catch(error => {

    $("#connection")
      .textContent =
      "API OFFLINE";

    $("#statusDot")
      .className =
      "status-dot offline";


    $("#matchList")
      .innerHTML = `

        <div class="empty">
          Backend unavailable.
          Start Spring Boot to load matches.
        </div>

      `;


    console.error(error);

  });

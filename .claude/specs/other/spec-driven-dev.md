# Spec Driven Development Process

1. Created the draft version of the `app-definition.md` sibling document.

2. Provided such draft version to Gemini and requested to create a Technical Specification Document (TSD) out of it using the following prompt:
   > Create a technical specification document for the app which description has been attached to the present conversation. I want this document to be the foundation to then build the app.
   > Consider that the app is currently in an MVP state and thus the document should reflect the simplest implementation possible. Therefore do not consider the "Post-MVP Services" section for now. Collect all potential issues and improvements in two respective sections; the next step will be resolving all the issues.
   > Do not fabricate information and do not jump to rush conclusions or make any assumption. If you are unsure about something, ask me for clarification. Finally, be concise and sacrifice grammar for the shake of concision.

3. Adapted this document to resolve some of the spotted issues and incorporated some of the collected improvements. Then, I requested Gemini to re-run the same prompt starting from the scratch and forgetting the previous conversation.

4. Requested Claude Code (CC) to format Gemini's final response adequately and included the output in a new `.claude/specs/SPEC.md` file.

5. Created a Java-Spring project in VS Code using the Initializr utility, executed `/init` with CC CLI.

6. Installed Context7, which in turn created the `find-docs` global skill at `~/.claude/skills/find-docs/SKILL.md` useful to extract tooling documentation.

7. Created the `DocsExplorer` subagent (inspired by the contents of this other [`DocsExplorer`](https://github.com/academind/claude-code-course-resources/blob/main/other/subagent/DocsExplorer.md) subagent) that uses `find-docs` as well as the `WebSearch` + `WebFetch` tools if the former returns no relevant data.

8. Installed the following skills at project's `.agents/skills` and `.claude/skills` (as symlinks) folders:
	- https://skills.sh/mattpocock/skills/tdd
	- https://skills.sh/ccheney/robust-skills/clean-ddd-hexagonal
	- https://skills.sh/booklib-ai/skills/effective-java
	- https://skills.sh/asyrafhussin/agent-skills/clean-code-principles
	- https://skills.sh/addyosmani/agent-skills/documentation-and-adrs
	- https://skills.sh/mattpocock/skills/grill-me

9. Added the following text to `CLAUDE.md`:
   > We're building the app described in `.claude/specs/SPEC.md`. Read that file for general architectural tasks or to double-check the exact database structure, tech stack or application architecture.
	 > Whenever working with any third-party library or something similar, you MUST look up the official documentation to ensure that you're working with up-to-date information. Use the DocsExplorer subagent for efficient documentation lookup.
	 > Keep your replies extremely concise and focus on conveying the key information. No unnecessary fluff, no long code snippets.
	 > Do not fabricate information and do not jump to rush conclusions or make any assumption. If you are unsure about something, ask me for clarification.
	 > Finally, be concise and sacrifice grammar for the shake of concision when creating any kind of documentation.

10. Plannification instructions:
	> You are an experienced Java/Spring developer that wants to build the application defined at `.claude/specs/SPEC.md`.
  > Make a multi-phase plan first. Use the grill-me skill to perform an exhaustive analysis of each phase. For those questions regarding a concrete phase that I cannot currently resolve produce a list of unresolved questions in a "Q&A" section at the end of the phase plan. Each phase must correspond to a GitHub issue.
	> Present the multi-phase plan before starting its implementation.
	> Use the clean-code-principles and clean-ddd-hexagonal skills where necessary.

11. Implementation instructions:
- Resolve any open question related to the phase first with CC:
	> Grab GH issue #X. First, lets resolve any open questions included in the "Q&A" section of the issue.
	> Update the "Q&A" section with the answers to those questions.

- When ready to start the phase implementation, request CC:
	> Proceed to the implementation of the GH issue #X using the TDD skill.
	> Remember to use the DocsExplorer subagent for efficient documentation lookup where necessary.

- Once the phase implementation is completed, request CC:
	> Create a closing comment for the GH issue #X that briefly explains what was built.
	> Include the hash of the commit where the changes were made.
	> Then, close the issue.

- After each phase tell CC to either `/clear` or `/compact` the main agent's context window.

12. Created ADRs and README files along the way following the [corresponding skill](https://www.skills.sh/addyosmani/agent-skills/documentation-and-adrs).

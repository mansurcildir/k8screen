<script lang="ts">
  import * as Collapsible from '$lib/components/ui/collapsible/index.js';
  import Spinner from '$lib/components/spinner.svelte';
  import ChevronRight from 'lucide-svelte/icons/chevron-right';
  import Button from '$lib/components/ui/button/button.svelte';
  import SquareTerminal from 'lucide-svelte/icons/square-terminal';
  import CodeXML from 'lucide-svelte/icons/code-xml';
  import Code from 'lucide-svelte/icons/code';
  import Logs from 'lucide-svelte/icons/logs';
  import Save from 'lucide-svelte/icons/save';
  import { OptionTerminal } from '$lib/model/enum';
  import HighlightBlock from './highlight-block.svelte';
  import TextArea from './text-area.svelte';

  export let k8sItem: string;
  export let details: string;
  export let logs: string = '';
  export let open: boolean;
  export let containerHeight = 300;
  export let type: 'deployment' | 'pod' | 'service' | 'secret' | 'stateful-set' | 'none' = 'none';

  export let getDetails: () => Promise<string>;
  export let updateItem: () => Promise<string>;
  export let getLogs: () => Promise<string> = async () => '';
  export let getExec: () => Promise<string> = async () => '';

  let wsLogs: WebSocket;
  let wsExec: WebSocket;
  let loading: boolean = false;
  let execReq: string = '';
  let execRes: string = '';
  let option: OptionTerminal = OptionTerminal.DETAIL;

  $: {
    editedItem = details;

    if (k8sItem) {
      if (option === OptionTerminal.LOG) {
        log();
      } else if (option === OptionTerminal.BASH) {
        exec();
      } else if (option === OptionTerminal.DETAIL) {
        detail();
      } else if (option === OptionTerminal.EDIT) {
        edit();
      }
    }
  }

  let editedItem: string;
  let isResizing = false;
  const maxContainerHeight = 500;
  const minContainerHeight = 100;

  const detail = async () => {
    loading = true;
    option = OptionTerminal.DETAIL;
    details = await getDetails();
    loading = false;
  };

  const edit = async () => {
    loading = true;
    option = OptionTerminal.EDIT;
    details = await getDetails();
    loading = false;
  };

  const save = async () => {
    editedItem = await updateItem();
  };

  const log = async () => {
    closeSocket(wsLogs);

    loading = true;
    open = true;
    const url = await getLogs();

    if (option !== OptionTerminal.LOG) {
      option = OptionTerminal.LOG;
    }

    if (!wsLogs || wsLogs.readyState !== WebSocket.OPEN) {
      wsLogs = new WebSocket(url);
    }

    wsLogs.onopen = function () {
      console.log('WebSocket connection opened!');
    };

    wsLogs.onmessage = function (event) {
      logs = event.data;
      loading = false;
    };

    wsLogs.onclose = function () {
      console.log('WebSocket connection closed!');
    };
  };

  const sendCommand = () => {
    if (wsExec && wsExec.readyState === WebSocket.OPEN && execReq.trim() !== '') {
      wsExec.send(execReq);
      execReq = '';
    }
  };

  const exec = async () => {
    closeSocket(wsExec);

    loading = true;
    open = true;
    const url = await getExec();

    if (option !== OptionTerminal.BASH) {
      option = OptionTerminal.BASH;
    }

    if (!wsExec || wsExec.readyState !== WebSocket.OPEN) {
      wsExec = new WebSocket(url);
    }

    wsExec.onopen = function () {
      console.log('WebSocket connection opened!');
    };

    wsExec.onmessage = function (event) {
      execRes = event.data;
      loading = false;
    };

    wsExec.onclose = function () {
      console.log('WebSocket connection closed!');
    };
  };

  const closeSocket = (socket: WebSocket | undefined) => {
    if (socket) {
      console.log('WebSocket is already open, closing it first.');
      socket.close();
    }
  };

  const handleMouseMove = (event: MouseEvent) => {
  event.preventDefault();
  if (open && isResizing) {
    const newHeight = containerHeight - event.movementY;
    if (newHeight <= maxContainerHeight && newHeight >= minContainerHeight) {
      containerHeight = newHeight;
    }
  }
};

  const handleMouseDown = () => {
    isResizing = true;
  };

   const handleMouseUp = () => {
    isResizing = false;
  };

  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('mouseup', handleMouseUp);
</script>

<style>
  .log-container {
    font-family: 'Courier New', Courier, monospace;
    white-space: pre-wrap;
    word-break: break-word;
  }
</style>

<Collapsible.Root class="group/collapsible absolute bottom-0 left-0 right-0 mx-4 rounded-t-md bg-sidebar" open={open}>
  <div class="flex items-center gap-2 px-3 py-1">
    <div class="flex items-center justify-center gap-2">
      <button on:click={() => (open = !open)}>
        <ChevronRight
          class="ml-auto size-4 transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90"
        />
      </button>
      <h1 style="font-family: 'Courier New', Courier, monospace;">
        {#if k8sItem && option == OptionTerminal.LOG}
          {k8sItem} [logs]
        {:else if k8sItem && option == OptionTerminal.BASH}
          {k8sItem} [sh]
        {:else if (k8sItem && option == OptionTerminal.DETAIL) || option == OptionTerminal.EDIT}
          {k8sItem}.yaml {option == OptionTerminal.DETAIL ? '[view]' : '[edit]'}
        {/if}
      </h1>
    </div>

    {#if k8sItem}
      <div class="ms-auto flex gap-2">
        {#if editedItem !== details}
          <Button
            variant="ghost"
            class=" h-auto rounded-lg bg-muted p-2 hover:bg-neutral-200"
            aria-label="Playground"
            onclick={save}
          >
            <Save class="size-5" />
          </Button>
        {/if}

        <Button
          variant="ghost"
          class="ms-auto h-auto rounded-lg bg-muted p-2 hover:bg-neutral-200 {option == OptionTerminal.DETAIL
            ? 'bg-neutral-200'
            : ''}"
          aria-label="Playground"
          onclick={() => (option = OptionTerminal.DETAIL)}
        >
          <CodeXML class="size-5" />
        </Button>

        <Button
          variant="ghost"
          class="h-auto rounded-lg bg-muted p-2 hover:bg-neutral-200 {option == OptionTerminal.EDIT
            ? 'bg-neutral-200'
            : ''}"
          aria-label="Playground"
          onclick={() => (option = OptionTerminal.EDIT)}
        >
          <Code class="size-5" />
        </Button>

        {#if type == 'pod'}
          <Button
            variant="ghost"
            class="h-auto rounded-lg bg-muted p-2 hover:bg-neutral-200 {option == OptionTerminal.LOG
              ? 'bg-neutral-200'
              : ''}"
            aria-label="Playground"
            onclick={() => (option = OptionTerminal.LOG)}
          >
            <Logs class="size-5" />
          </Button>

          <Button
            variant="ghost"
            class="h-auto rounded-lg bg-muted p-2 hover:bg-neutral-200 {option == OptionTerminal.BASH
              ? 'bg-neutral-200'
              : ''}"
            aria-label="Playground"
            onclick={() => (option = OptionTerminal.BASH)}
          >
            <SquareTerminal class="size-5" />
          </Button>
        {/if}
      </div>
    {/if}
  </div>

  <div
    class="resize-handle"
    role="button"
    tabindex="0"
    on:mousedown={handleMouseDown}
    style="cursor: ns-resize; height: 5px; background: gray; left: 0; right: 0;"
  ></div>
  <Collapsible.Content>
    <div class="log-container relative bg-black text-white" style="height: {containerHeight}px;">
      {#if loading}
        <div class="flex h-full items-center justify-center">
          <Spinner class="m-auto h-10 w-10" />
        </div>
      {:else if option === OptionTerminal.LOG}
        <div class="h-full w-full overflow-auto p-5">
          <HighlightBlock code={logs} language="1c"/>
        </div>
      {:else if option === OptionTerminal.BASH}
        <div class="flex h-full flex-col p-5">
          {execRes}
          <div class="flex w-full items-center overflow-auto">
            <form on:submit={async () => sendCommand()} class="flex w-full items-center">
              <input
                type="text"
                bind:value={execReq}
                class="no-animation w-full resize-none bg-black text-white outline-none"
              />
            </form>
          </div>
        </div>
      {:else if option === OptionTerminal.DETAIL}
        <div class="h-full w-full overflow-y-auto overflow-x-hidden px-5">
          <HighlightBlock code={details} language="yaml"/>
        </div>
      {:else if option === OptionTerminal.EDIT}
        <div class="h-full w-full overflow-y-auto overflow-x-hidden px-5">
        <TextArea code={details} language="yaml"/>
        </div>
      {/if}
    </div>
  </Collapsible.Content>
</Collapsible.Root>

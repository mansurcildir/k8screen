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
  import { onMount } from 'svelte';
  import { stringTag } from 'yaml/util';

  export let k8sItem: string;
  export let option: OptionTerminal;
  export let details: string;
  export let logs: string = '';
  export let execReq: string = '';
  export let execRes: string = '';
  export let loading: boolean;
  export let open: boolean;
  export let containerHeight = 300;
  export let type: 'deployment' | 'pod' | 'service' | 'secret' | 'stateful-set' | 'none' = 'none';

  export let getDetails: (pod: string, opt: OptionTerminal) => Promise<string>;
  export let getLogs: (pod: string, opt: OptionTerminal) => Promise<string> = async () => '';
  export let updateItem: (pod: string) => Promise<string>;
  export let exec: (req: string) => Promise<string> = async () => '';

  $: {
    editedItem = details;
  }

  let editedItem: string;
  let isResizing = false;
  const maxContainerHeight = 500;
  const minContainerHeight = 100;

  const detail = async () => {
    details = await getDetails(k8sItem, OptionTerminal.DETAIL);
  };

  const edit = async () => {
    details = await getDetails(k8sItem, OptionTerminal.EDIT);
  };

  const save = async () => {
    editedItem = await updateItem(k8sItem);
  };

  const log = async () => {
    loading = true;
    open = true;
    logs = await getLogs(k8sItem, OptionTerminal.LOG);
    loading = false;
  };

  const terminal = async () => {
    open = true;
    option = OptionTerminal.BASH;
  };

  const handleMouseDown = () => {
    isResizing = true;
  };

  const handleMouseMove = (event: MouseEvent) => {
    if (isResizing) {
      const newHeight = containerHeight - event.movementY;
      if (newHeight <= maxContainerHeight && newHeight >= minContainerHeight) {
        if (newHeight < containerHeight) {
          const isAtTop = window.innerHeight - (containerHeight - event.movementY) <= 150;
          if (!isAtTop) {
            containerHeight = newHeight;
          }
        } else {
          containerHeight = newHeight;
        }
      }
    }
  };

  const handleMouseUp = () => {
    isResizing = false;
  };

  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('mouseup', handleMouseUp);

  onMount(() => {
    setInterval(async () => {
      if (option === OptionTerminal.LOG) {
        logs = await getLogs(k8sItem, OptionTerminal.LOG);
      }
    }, 5000);
  });
</script>

<style>
  .log-container {
    font-family: 'Courier New', Courier, monospace;
    white-space: pre-wrap;
    overflow-wrap: break-word;
    word-break: break-word;
  }
</style>

<Collapsible.Root class="group/collapsible rounded-t-md bg-sidebar" open={open}>
  <div class="flex items-center px-3 py-1 gap-2">
    <div class="flex justify-center items-center gap-2">
      <button on:click={() => (open = !open)}>
        <ChevronRight
          class="ml-auto transition-transform duration-200 group-data-[state=open]/collapsible:rotate-90 size-4"
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
      <div class="flex ms-auto gap-2">
        {#if editedItem !== details}
          <Button variant="ghost" class=" bg-muted rounded-lg p-2 h-auto" aria-label="Playground" onclick={save}>
            <Save class="size-5" />
          </Button>
        {/if}

        <Button variant="ghost" class="ms-auto bg-muted rounded-lg p-2 h-auto" aria-label="Playground" onclick={detail}>
          <CodeXML class="size-5" />
        </Button>

        <Button variant="ghost" class="bg-muted rounded-lg p-2 h-auto" aria-label="Playground" onclick={edit}>
          <Code class="size-5" />
        </Button>

        {#if type == 'pod'}
          <Button variant="ghost" class="bg-muted rounded-lg p-2 h-auto" aria-label="Playground" onclick={log}>
            <Logs class="size-5" />
          </Button>

          <Button variant="ghost" class="bg-muted rounded-lg p-2 h-auto" aria-label="Playground" onclick={terminal}>
            <SquareTerminal class="size-5" />
          </Button>
        {/if}
      </div>
    {/if}
  </div>

  <Collapsible.Content>
    <div
      class="resize-handle"
      role="button"
      tabindex="0"
      on:mousedown={handleMouseDown}
      style="cursor: ns-resize; height: 5px; background: gray; left: 0; right: 0;"
    ></div>
    <div class="relative text-white log-container rounded-b-md bg-black" style="height: {containerHeight}px;">
      {#if loading}
        <div class="flex justify-center items-center h-full">
          <Spinner />
        </div>
      {:else if option === OptionTerminal.LOG}
        <div class="w-full h-full p-5 overflow-auto">{logs}</div>
      {:else if option === OptionTerminal.BASH}
        <div class="flex flex-col justify-between h-full p-5">
          {execRes}
          <div class="flex items-center w-full overflow-auto">
            >
            <form on:submit={async () => (execRes = await exec(execReq))} class="flex items-center w-full">
              <input
                type="text"
                bind:value={execReq}
                class="w-full bg-black text-white resize-none outline-none no-animation"
              />
            </form>
          </div>
        </div>
      {:else if option === OptionTerminal.DETAIL}
        <div class="w-full h-full p-5 overflow-auto">{details}</div>
      {:else if option === OptionTerminal.EDIT}
        <textarea bind:value={editedItem} class="w-full h-full p-5 bg-black overflow-auto resize-none outline-none"
        ></textarea>
      {/if}
    </div>
  </Collapsible.Content>
</Collapsible.Root>
